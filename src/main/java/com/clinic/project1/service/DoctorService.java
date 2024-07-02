package com.clinic.project1.service;


import com.clinic.project1.exception.DoctorWithIdNotFoundException;
import com.clinic.project1.mapper.DoctorMapper;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.command.create.CreateDoctorCommand;
import com.clinic.project1.model.command.update.FullUpdateDoctorCommand;
import com.clinic.project1.model.command.update.UpdateDoctorSpecializationCommand;
import com.clinic.project1.model.dto.DoctorDto;
import com.clinic.project1.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {


    private final DoctorRepository doctorRepository;

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public DoctorDto save(CreateDoctorCommand command) {
        Doctor doctor = DoctorMapper.mapFromCommand(command);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return DoctorMapper.mapToDto(savedDoctor);
    }

    public DoctorDto findById(int id) {
        return doctorRepository.findById(id)
                .map(DoctorMapper::mapToDto)
                .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                        .format("Doctor with id {0} not found", id)));
    }

    public DoctorDto update(int id, FullUpdateDoctorCommand command) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                        .format("Doctor with id {0} not found", id)));
        if (command.getFirstName() != null) {
            doctor.setFirstName(command.getFirstName());
        }
        if (command.getLastName() != null) {
            doctor.setLastName(command.getLastName());
        }
        if (command.getSpecializations() != null) {
            doctor.setSpecializations(command.getSpecializations());
        }

        return DoctorMapper.mapToDto(doctorRepository.save(doctor));
    }


    public DoctorDto updateSpecialization(int id, UpdateDoctorSpecializationCommand command) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                        .format("Doctor with id {0} not found", id)));
        doctor.setSpecializations(command.getSpecializations());
        return DoctorMapper.mapToDto(doctorRepository.save(doctor));
    }

    public void deleteById(int id) {
        doctorRepository.deleteById(id);
    }
}
