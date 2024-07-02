package com.clinic.project1.service;

import com.clinic.project1.common.Disease;
import com.clinic.project1.common.Specialization;
import com.clinic.project1.exception.PatientWithIdNotFoundException;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreatePatientCommand;
import com.clinic.project1.model.command.update.FullPatientUpdateCommand;
import com.clinic.project1.model.command.update.UpdatePatientDiseaseCommand;
import com.clinic.project1.model.dto.PatientDto;
import com.clinic.project1.repository.DoctorRepository;
import com.clinic.project1.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @InjectMocks
    private PatientService patientService;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Captor
    private ArgumentCaptor<Patient> patientArgumentCaptor;

    @Test
    void testFindAll_ReturnListOfPatientDtos() {
        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor")
                .lastName("Lastname")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .build();

        List<Patient> patients = List.of(
                Patient.builder()
                        .id(1)
                        .firstName("Patient1")
                        .lastName("LastName1")
                        .disease(Disease.BRONCHITIS)
                        .doctor(doctor)
                        .build(),
                Patient.builder()
                        .id(2)
                        .firstName("Patient2")
                        .lastName("LastName2")
                        .disease(Disease.FLU)
                        .doctor(doctor)
                        .build()
        );
        when(patientRepository.findAll()).thenReturn(patients);

        List<PatientDto> patientDtoList = patientService.findAll();

        assertNotNull(patientDtoList);
        assertEquals(patients.size(), patientDtoList.size());
        verify(patientRepository).findAll();
    }

    @Test
    void testSave_NewPatientSaved_ResultsInPatientBeingSaved() {
        CreatePatientCommand command = CreatePatientCommand.builder()
                .firstName("Name")
                .lastName("LastName")
                .disease(Disease.BRONCHITIS)
                .doctorId(1)
                .build();

        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor")
                .lastName("Lastname")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .build();

        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.of(doctor));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        PatientDto savedPatientDto = patientService.save(command);

        verify(patientRepository).save(patientArgumentCaptor.capture());
        Patient savedPatient = patientArgumentCaptor.getValue();
        assertEquals(command.getFirstName(), savedPatient.getFirstName());
        assertEquals(command.getLastName(), savedPatient.getLastName());
        assertEquals(command.getDisease(), savedPatient.getDisease());
        assertEquals(command.getDoctorId(), savedPatient.getDoctor().getId());
    }

    @Test
    void testFindById_ValidId_ReturnPatientDto() {
        int id = 1;
        Patient patient = Patient.builder()
                .id(id)
                .firstName("Name")
                .lastName("LastName")
                .disease(Disease.BRONCHITIS)
                .doctor(Doctor.builder().id(1).build())
                .build();

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        PatientDto patientDto = patientService.findById(id);

        assertNotNull(patientDto);
        assertEquals(patient.getId(), patientDto.getId());
        assertEquals(patient.getFirstName(), patientDto.getFirstName());
        assertEquals(patient.getLastName(), patientDto.getLastName());
        assertEquals(patient.getDisease(), patientDto.getDisease());
        assertEquals(patient.getDoctor().getId(), patientDto.getDoctorId());
    }

    @Test
    void testFindById_PatientNotFound_ResultsInPatientWithIdNotFoundException() {
        int id = 1;
        String exceptionMsg = "Patient with id " + id + " not found";
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        PatientWithIdNotFoundException exception = assertThrows(
                PatientWithIdNotFoundException.class,
                () -> patientService.findById(id)
        );

        assertEquals(exceptionMsg, exception.getMessage());

        verify(patientRepository).findById(id);
    }

    @Test
    void testUpdate_PatientFound_ResultsInPatientBeingUpdated() {
        int patientId = 1;
        Patient patientToUpdate = Patient.builder()
                .id(patientId)
                .firstName("OldName")
                .lastName("OldLastName")
                .disease(Disease.BRONCHITIS)
                .doctor(Doctor.builder().id(1).build())
                .build();
        FullPatientUpdateCommand command = new FullPatientUpdateCommand();
        command.setFirstName("NewName");
        command.setLastName("NewLastName");
        command.setDisease(Disease.COVID_19);
        command.setDoctorId(2);

        Doctor newDoctor = Doctor.builder()
                .id(2)
                .firstName("NewDoctor")
                .lastName("NewLastname")
                .specializations(Set.of(Specialization.FAMILY_MEDICINE))
                .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patientToUpdate));
        when(doctorRepository.findById(command.getDoctorId())).thenReturn(Optional.of(newDoctor));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        PatientDto updatedPatientDto = patientService.update(patientId, command);

        assertEquals(patientToUpdate.getId(), updatedPatientDto.getId());

        verify(patientRepository).findById(patientId);
        verify(doctorRepository).findById(command.getDoctorId());
        verify(patientRepository).save(patientArgumentCaptor.capture());
        Patient updatedPatient = patientArgumentCaptor.getValue();
        assertEquals(command.getFirstName(), updatedPatient.getFirstName());
        assertEquals(command.getLastName(), updatedPatient.getLastName());
        assertEquals(command.getDisease(), updatedPatient.getDisease());
        assertEquals(command.getDoctorId(), updatedPatient.getDoctor().getId());
    }

    @Test
    void testUpdateDisease_PatientDiseaseUpdated_ResultsInPatientDtoReturned() {
        int patientId = 1;
        Patient existingPatient = Patient.builder()
                .id(patientId)
                .firstName("Name")
                .lastName("LastName")
                .disease(Disease.FLU)
                .doctor(Doctor.builder()
                        .id(1)
                        .firstName("Doctor")
                        .lastName("Lastname")
                        .specializations(Set.of(Specialization.FAMILY_MEDICINE))
                        .build())
                .build();
        UpdatePatientDiseaseCommand command = new UpdatePatientDiseaseCommand();
        command.setDisease(Disease.COVID_19);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        PatientDto updatedPatientDto = patientService.updateDisease(patientId, command);

        assertEquals(existingPatient.getId(), updatedPatientDto.getId());

        verify(patientRepository).findById(patientId);
        verify(patientRepository).save(patientArgumentCaptor.capture());
        Patient updatedPatient = patientArgumentCaptor.getValue();
        assertEquals(existingPatient.getFirstName(), updatedPatient.getFirstName());
        assertEquals(existingPatient.getLastName(), updatedPatient.getLastName());
        assertEquals(command.getDisease(), updatedPatient.getDisease());
    }

    @Test
    void testSoftDeleteById_PatientDeleted() {
        int patientId = 1;

        patientService.deleteById(patientId);

        verify(patientRepository).deleteById(patientId);
    }
}
