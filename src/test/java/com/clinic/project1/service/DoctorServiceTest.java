package com.clinic.project1.service;

import com.clinic.project1.common.Specialization;
import com.clinic.project1.exception.DoctorWithIdNotFoundException;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.command.create.CreateDoctorCommand;
import com.clinic.project1.model.command.update.FullUpdateDoctorCommand;
import com.clinic.project1.model.command.update.UpdateDoctorSpecializationCommand;
import com.clinic.project1.model.dto.DoctorDto;
import com.clinic.project1.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Captor
    private ArgumentCaptor<Doctor> doctorArgumentCaptor;


    @Test
    void testSave_NewDoctorSaved_ResultsInDoctorDtoReturned() {
        CreateDoctorCommand command = CreateDoctorCommand
                .builder()
                .firstName("John")
                .lastName("Doe")
                .specializationSet(Set.of(Specialization.PEDIATRICS))
                .build();
        Doctor savedDoctor = Doctor.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .specializations(Set.of(Specialization.PEDIATRICS))
                .build();
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        DoctorDto savedDoctorDto = doctorService.save(command);

        assertEquals(savedDoctor.getId(), savedDoctorDto.getId());
        assertEquals(savedDoctor.getFirstName(), savedDoctorDto.getFirstName());
        assertEquals(savedDoctor.getLastName(), savedDoctorDto.getLastName());
        assertEquals(savedDoctor.getSpecializations(), savedDoctorDto.getSpecializations());

        verify(doctorRepository).save(doctorArgumentCaptor.capture());
        Doctor newDoctor = doctorArgumentCaptor.getValue();
        assertEquals(command.getFirstName(), newDoctor.getFirstName());
        assertEquals(command.getLastName(), newDoctor.getLastName());
        assertEquals(command.getSpecializationSet(), newDoctor.getSpecializations());
    }

    @Test
    void testFindById_DoctorFound_ResultsInDoctorDtoReturned() {
        int doctorId = 1;
        Doctor doctor = Doctor.builder()
                .id(doctorId)
                .firstName("Mark")
                .lastName("Code")
                .specializations(Set.of(Specialization.PEDIATRICS))
                .build();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        DoctorDto doctorDto = doctorService.findById(doctorId);

        assertEquals(doctor.getId(), doctorDto.getId());
        assertEquals(doctor.getFirstName(), doctorDto.getFirstName());
        assertEquals(doctor.getLastName(), doctorDto.getLastName());
        assertEquals(doctor.getSpecializations(), doctorDto.getSpecializations());

        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void testFindById_DoctorNotFound_ResultsInDoctorWithIdNotFoundException() {
        int doctorId = 1;
        String exceptionMsg = "Doctor with id " + doctorId + " not found";
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        DoctorWithIdNotFoundException exception = assertThrows(
                DoctorWithIdNotFoundException.class,
                () -> doctorService.findById(doctorId)
        );

        assertEquals(exceptionMsg, exception.getMessage());

        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void testUpdate_DoctorFound_ResultsInDoctorBeingUpdated() {
        int doctorId = 1;
        Doctor doctorToUpdate = Doctor.builder()
                .id(doctorId)
                .firstName("OldName")
                .lastName("OldLastName")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .build();
        FullUpdateDoctorCommand command = FullUpdateDoctorCommand
                .builder()
                .firstName("Marek")
                .lastName("Ziemniak")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .build();
        command.setSpecializations(Set.of(Specialization.FAMILY_MEDICINE));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctorToUpdate));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        DoctorDto updatedDoctorDto = doctorService.update(doctorId, command);

        assertEquals(doctorToUpdate.getId(), updatedDoctorDto.getId());

        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).save(doctorArgumentCaptor.capture());
        Doctor updatedDoctor = doctorArgumentCaptor.getValue();
        assertEquals(command.getFirstName(), updatedDoctor.getFirstName());
        assertEquals(command.getLastName(), updatedDoctor.getLastName());
        assertEquals(command.getSpecializations(), updatedDoctor.getSpecializations());
    }

    @Test
    void testUpdateSpecialization_DoctorSpecializationUpdated_ResultsInDoctorReturned() {
        int doctorId = 1;
        Doctor existingDoctor = Doctor.builder()
                .id(doctorId)
                .firstName("Name")
                .lastName("LastName")
                .specializations(Set.of(Specialization.PEDIATRICS))
                .build();
        UpdateDoctorSpecializationCommand command = new UpdateDoctorSpecializationCommand();
        command.setSpecializations(Set.of(Specialization.PULMONOLOGY, Specialization.FAMILY_MEDICINE));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        DoctorDto updatedDoctorDto = doctorService.updateSpecialization(doctorId, command);

        assertEquals(existingDoctor.getId(), updatedDoctorDto.getId());

        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).save(doctorArgumentCaptor.capture());
        Doctor updatedDoctor = doctorArgumentCaptor.getValue();
        assertEquals(existingDoctor.getFirstName(), updatedDoctor.getFirstName());
        assertEquals(existingDoctor.getLastName(), updatedDoctor.getLastName());
        assertEquals(command.getSpecializations(), updatedDoctor.getSpecializations());
    }

    @Test
    void testSoftDeleteById_DoctorDeleted() {
        int doctorId = 1;

        doctorService.deleteById(doctorId);

        verify(doctorRepository).deleteById(doctorId);
    }

    @Test
    void testFindAll_ReturnsListOfDoctorDto() {
        List<Doctor> doctors = Arrays.asList(
                Doctor.builder()
                        .id(1)
                        .firstName("John")
                        .lastName("Doe")
                        .specializations(Set.of(Specialization.PEDIATRICS))
                        .build(),
                Doctor.builder()
                        .id(2)
                        .firstName("Jane")
                        .lastName("Smith")
                        .specializations(Set.of(Specialization.FAMILY_MEDICINE))
                        .build()
        );
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<DoctorDto> doctorDtos = doctorService.findAll();

        assertNotNull(doctorDtos);
        assertEquals(doctors.size(), doctorDtos.size());
        verify(doctorRepository).findAll();
    }


}
