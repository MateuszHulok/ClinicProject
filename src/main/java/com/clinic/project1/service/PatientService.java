package com.clinic.project1.service;


import com.clinic.project1.common.Disease;
import com.clinic.project1.common.Specialization;
import com.clinic.project1.exception.DoctorWithIdNotFoundException;
import com.clinic.project1.exception.InvalidSpecializationException;
import com.clinic.project1.exception.PatientWithIdNotFoundException;
import com.clinic.project1.mapper.PatientMapper;
import com.clinic.project1.mapper.SpecializationMapper;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreatePatientCommand;
import com.clinic.project1.model.command.update.FullPatientUpdateCommand;
import com.clinic.project1.model.command.update.UpdatePatientDiseaseCommand;
import com.clinic.project1.model.dto.PatientDto;
import com.clinic.project1.repository.DoctorRepository;
import com.clinic.project1.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public List<PatientDto> findAll() {
        return patientRepository.findAll()
                .stream()
                .map(PatientMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientDto save(CreatePatientCommand command) {
        Doctor doctor = doctorRepository.findById(command.getDoctorId())
                .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                        .format("Doctor with id {0} not found", command.getDoctorId())));

        validateDoctorSpecialization(doctor, command.getDisease());

        Patient toSave = PatientMapper.mapFromCommand(command, doctor);
        Patient savedPatient = patientRepository.save(toSave);

        return PatientMapper.mapToDto(savedPatient);
    }

    public PatientDto findById(int id) {
        return patientRepository.findById(id)
                .map(PatientMapper::mapToDto)
                .orElseThrow(() -> new PatientWithIdNotFoundException(MessageFormat
                        .format("Patient with id {0} not found", id)));
    }

    public PatientDto update(int id, FullPatientUpdateCommand command) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientWithIdNotFoundException(MessageFormat
                        .format("Patient with id {0} not found", id)));

        if (command.getFirstName() != null) {
            patient.setFirstName(command.getFirstName());
        }
        if (command.getLastName() != null) {
            patient.setLastName(command.getLastName());
        }
        if (command.getDisease() != null) {
            patient.setDisease(command.getDisease());
        }

        int doctorId = command.getDoctorId();
        if (doctorId != 0) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                            .format("Doctor with id {0} not found", doctorId)));

            Specialization requiredSpecialization = SpecializationMapper.getRequiredSpecializationForDisease(
                    command.getDisease() != null ? command.getDisease() : patient.getDisease());

            if (!doctor.getSpecializations().contains(requiredSpecialization)) {
                throw new InvalidSpecializationException(MessageFormat
                        .format("Doctor with id {0} does not have the required specialization for disease {1}",
                                doctorId, command.getDisease() != null ? command.getDisease() : patient.getDisease()));
            }
            patient.setDoctor(doctor);
        } else {
            Specialization requiredSpecialization = SpecializationMapper.getRequiredSpecializationForDisease(
                    command.getDisease() != null ? command.getDisease() : patient.getDisease());

            if (command.getDisease() != null && !patient.getDoctor().getSpecializations().contains(requiredSpecialization)) {
                throw new InvalidSpecializationException(MessageFormat
                        .format("Doctor with id {0} does not have the required specialization for disease {1}",
                                patient.getDoctor().getId(), command.getDisease()));
            }
        }

        return PatientMapper.mapToDto(patientRepository.save(patient));
    }

    public PatientDto updateDisease(int id, UpdatePatientDiseaseCommand command) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientWithIdNotFoundException(MessageFormat
                        .format("Patient with id {0} not found", id)));

        Specialization requiredSpecialization = SpecializationMapper.getRequiredSpecializationForDisease(command.getDisease());

        if (!patient.getDoctor().getSpecializations().contains(requiredSpecialization)) {
            throw new InvalidSpecializationException(MessageFormat
                    .format("Doctor with id {0} does not have the required specialization for disease {1}",
                            patient.getDoctor().getId(), command.getDisease()));
        }

        patient.setDisease(command.getDisease());
        return PatientMapper.mapToDto(patientRepository.save(patient));
    }

    public void deleteById(int id) {
        patientRepository.deleteById(id);
    }

    private void validateDoctorSpecialization(Doctor doctor, Disease disease) {
        Specialization requiredSpecialization = SpecializationMapper.getRequiredSpecializationForDisease(disease);
        if (doctor.getSpecializations() == null || !doctor.getSpecializations().contains(requiredSpecialization)) {
            throw new InvalidSpecializationException(MessageFormat.format(
                    "Doctor with id {0} does not have the required specialization for disease {1}",
                    doctor.getId(), disease));
        }
    }
}