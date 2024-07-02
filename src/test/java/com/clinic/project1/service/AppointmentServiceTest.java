package com.clinic.project1.service;

import com.clinic.project1.common.Disease;
import com.clinic.project1.model.Appointment;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreateAppointmentCommand;
import com.clinic.project1.model.command.update.FullUpdateAppointmentCommand;
import com.clinic.project1.model.command.update.UpdateAppointmentDateCommand;
import com.clinic.project1.model.dto.AppointmentDto;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;

import com.clinic.project1.repository.AppointmentRepository;
import com.clinic.project1.repository.DoctorRepository;
import com.clinic.project1.repository.PatientRepository;
import com.clinic.project1.exception.AppointmentWithIdNotFoundException;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Captor
    private ArgumentCaptor<Appointment> appointmentArgumentCaptor;

    @Test
    void testFindAll_ReturnsListOfAppointmentDtos() {
        Doctor doctor = Doctor.builder().id(1).build();
        Patient patient = Patient.builder().id(1).build();

        List<Appointment> appointments = List.of(
                Appointment.builder().id(1).doctor(doctor).patient(patient).build(),
                Appointment.builder().id(2).doctor(doctor).patient(patient).build()
        );
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentDto> appointmentDtoList = appointmentService.findAll();

        assertNotNull(appointmentDtoList);
        assertEquals(appointments.size(), appointmentDtoList.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    void testSave_NewAppointmentSaved_ResultsInAppointmentBeingSaved() {
        CreateAppointmentCommand command = CreateAppointmentCommand.builder()
                .doctorId(1)
                .patientId(1)
                .date(LocalDateTime.now().plusDays(1))
                .build();

        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor")
                .lastName("Lastname")
                .appointments(Set.of())
                .build();

        Patient patient = Patient.builder()
                .id(1)
                .firstName("Patient")
                .lastName("Lastname")
                .disease(Disease.BRONCHITIS)
                .build();

        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(command.getPatientId())).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        AppointmentDto savedAppointmentDto = appointmentService.save(command);

        verify(appointmentRepository).save(appointmentArgumentCaptor.capture());
        Appointment savedAppointment = appointmentArgumentCaptor.getValue();
        assertEquals(command.getDoctorId(), savedAppointment.getDoctor().getId());
        assertEquals(command.getPatientId(), savedAppointment.getPatient().getId());
        assertEquals(command.getDate(), savedAppointment.getAppointmentDate());
    }

    @Test
    void testFindById_ValidId_ReturnsAppointmentDto() {
        int id = 1;
        Appointment appointment = Appointment.builder()
                .id(id)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .doctor(Doctor.builder().id(1).build())
                .patient(Patient.builder().id(1).build())
                .build();

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        AppointmentDto appointmentDto = appointmentService.findById(id);

        assertNotNull(appointmentDto);
        assertEquals(appointment.getId(), appointmentDto.getId());
        assertEquals(appointment.getAppointmentDate(), appointmentDto.getAppointmentDate());
        assertEquals(appointment.getDoctor().getId(), appointmentDto.getDoctorId());
        assertEquals(appointment.getPatient().getId(), appointmentDto.getPatientId());
    }

    @Test
    void testFindById_InvalidId_ThrowsAppointmentWithIdNotFoundException() {
        int id = 1;
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AppointmentWithIdNotFoundException.class, () -> appointmentService.findById(id));

        verify(appointmentRepository).findById(id);
    }

    @Test
    void testUpdate_ValidId_AppointmentUpdated() {
        int appointmentId = 1;
        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .doctor(Doctor.builder().id(1).build())
                .patient(Patient.builder().id(1).build())
                .build();

        FullUpdateAppointmentCommand command = FullUpdateAppointmentCommand.builder()
                .doctorId(2)
                .patientId(2)
                .appointmentDate(LocalDateTime.now().plusDays(2))
                .build();

        Doctor newDoctor = Doctor.builder()
                .id(2)
                .firstName("NewDoctor")
                .lastName("NewLastname")
                .appointments(Set.of())
                .build();

        Patient newPatient = Patient.builder()
                .id(2)
                .firstName("NewPatient")
                .lastName("NewLastname")
                .disease(Disease.FLU)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.of(newDoctor));
        when(patientRepository.findById(command.getPatientId())).thenReturn(Optional.of(newPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        AppointmentDto updatedAppointmentDto = appointmentService.update(appointmentId, command);

        assertEquals(appointment.getId(), updatedAppointmentDto.getId());

        verify(appointmentRepository).findById(appointmentId);
        verify(doctorRepository).findById(command.getDoctorId());
        verify(patientRepository).findById(command.getPatientId());
        verify(appointmentRepository).save(appointmentArgumentCaptor.capture());
        Appointment updatedAppointment = appointmentArgumentCaptor.getValue();
        assertEquals(command.getAppointmentDate(), updatedAppointment.getAppointmentDate());
        assertEquals(command.getDoctorId(), updatedAppointment.getDoctor().getId());
        assertEquals(command.getPatientId(), updatedAppointment.getPatient().getId());
    }

    @Test
    void testUpdateDate_ValidId_AppointmentDateUpdated() {
        int appointmentId = 1;
        Doctor doctor = Doctor.builder()
                .id(1)
                .appointments(new HashSet<>())
                .build();
        Patient patient = Patient.builder()
                .id(1)
                .build();
        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .doctor(doctor)
                .patient(patient)
                .build();
        doctor.getAppointments().add(appointment);

        UpdateAppointmentDateCommand command = new UpdateAppointmentDateCommand();
        command.setDateTime(LocalDateTime.now().plusDays(2));

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        AppointmentDto updatedAppointmentDto = appointmentService.updateDate(appointmentId, command);

        assertNotNull(updatedAppointmentDto);
        assertEquals(appointment.getId(), updatedAppointmentDto.getId());
        assertEquals(command.getDateTime(), updatedAppointmentDto.getAppointmentDate());

        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).save(appointmentArgumentCaptor.capture());
        Appointment updatedAppointment = appointmentArgumentCaptor.getValue();
        assertEquals(command.getDateTime(), updatedAppointment.getAppointmentDate());
    }

    @Test
    void testDeleteById_ValidId_AppointmentDeleted() {
        int appointmentId = 1;
        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .doctor(Doctor.builder().id(1).build())
                .patient(Patient.builder().id(1).build())
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.deleteById(appointmentId);

        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).deleteById(appointmentId);
    }
}
