package com.clinic.project1.service;


import com.clinic.project1.exception.*;
import com.clinic.project1.mapper.AppointmentMapper;
import com.clinic.project1.model.Appointment;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreateAppointmentCommand;
import com.clinic.project1.model.command.update.FullUpdateAppointmentCommand;
import com.clinic.project1.model.command.update.UpdateAppointmentDateCommand;
import com.clinic.project1.model.dto.AppointmentDto;
import com.clinic.project1.repository.AppointmentRepository;
import com.clinic.project1.repository.DoctorRepository;
import com.clinic.project1.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public List<AppointmentDto> findAll() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDto save(CreateAppointmentCommand command) {
        Doctor doctor = doctorRepository.findById(command.getDoctorId())
                .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                        .format("Doctor with id {0} not found", command.getDoctorId())));
        validateDoctorAvailability(doctor, command.getDate());
        Patient patient = patientRepository.findById(command.getPatientId())
                .orElseThrow(() -> new PatientWithIdNotFoundException(MessageFormat
                        .format("Patient with id {0} not found", command.getPatientId())));

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDate(command.getDate())
                .build();
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentMapper.mapToDto(savedAppointment);
    }

    public AppointmentDto findById(int id) {
        return appointmentRepository.findById(id)
                .map(AppointmentMapper::mapToDto)
                .orElseThrow(() -> new AppointmentWithIdNotFoundException(MessageFormat
                        .format("Appointment with id {0} not found", id)));
    }

    @Transactional
    public AppointmentDto update(int id, FullUpdateAppointmentCommand command) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentWithIdNotFoundException(MessageFormat
                        .format("Appointment with id {0} not found", id)));
        validateDateIsNotInThePast(appointment.getAppointmentDate());
        if (command.getAppointmentDate() != null) {
            appointment.setAppointmentDate(command.getAppointmentDate());
        }

        if (command.getDoctorId() != 0) {
            Doctor doctor = doctorRepository.findById(command.getDoctorId())
                    .orElseThrow(() -> new DoctorWithIdNotFoundException(MessageFormat
                            .format("Doctor with id {0} not found", command.getDoctorId())));
            if (command.getAppointmentDate() != null) {
                validateDoctorAvailability(doctor, command.getAppointmentDate());
            } else {
                validateDoctorAvailability(doctor, appointment.getAppointmentDate());
            }
            appointment.setDoctor(doctor);
        } else {
            if (command.getAppointmentDate() != null) {
                validateDoctorAvailability(appointment.getDoctor(), command.getAppointmentDate());
            }
        }

        if (command.getPatientId() != 0) {
            Patient patient = patientRepository.findById(command.getPatientId())
                    .orElseThrow(() -> new PatientWithIdNotFoundException(MessageFormat
                            .format("Student with id {0} not found", command.getPatientId())));
            appointment.setPatient(patient);
        }

        return AppointmentMapper.mapToDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDto updateDate(int id, UpdateAppointmentDateCommand command) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentWithIdNotFoundException(MessageFormat
                        .format("Appointment with id {0} not found", id)));
        validateDoctorAvailability(appointment.getDoctor(), command.getDateTime());
        validateDateIsNotInThePast(appointment.getAppointmentDate());

        appointment.setAppointmentDate(command.getDateTime());

        return AppointmentMapper.mapToDto(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteById(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentWithIdNotFoundException(MessageFormat
                        .format("Appointment with id {0} not found", id)));
        validateDateIsNotInThePast(appointment.getAppointmentDate());
        appointmentRepository.deleteById(id);
    }

    private void validateDateIsNotInThePast(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new DateInThePastExcpetion(MessageFormat
                    .format("Date {0} is in the past", dateTime));
        }
    }

    private void validateDoctorAvailability(Doctor doctor, LocalDateTime appointmentDate) {
        Set<Appointment> appointmentSet = doctor.getAppointments();
        if (appointmentSet != null && appointmentSet
                .stream()
                .map(Appointment::getAppointmentDate)
                .anyMatch(i -> i.equals(appointmentDate))) {
            throw new DoctorOccupiedException(MessageFormat
                    .format("Doctor has an appointment scheduled for date {0}", appointmentDate));
        }
    }
}
