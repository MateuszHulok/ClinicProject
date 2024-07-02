package com.clinic.project1.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clinic.project1.common.Disease;
import com.clinic.project1.common.Specialization;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.repository.DoctorRepository;
import com.clinic.project1.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void testFindAll_ResultsInPatientListBeingReturned() throws Exception {

        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor1")
                .lastName("DoctorLastName1")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .build();
        doctorRepository.save(doctor);

        Patient patient = Patient.builder()
                .id(1)
                .firstName("Patient1")
                .lastName("LastName1")
                .disease(Disease.BRONCHITIS)
                .doctor(doctor)
                .build();
        patientRepository.save(patient);

        mockMvc.perform(get("/api/v1/patients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(patient.getId()))
                .andExpect(jsonPath("$[0].firstName").value(patient.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(patient.getLastName()))
                .andExpect(jsonPath("$[0].disease").value(patient.getDisease().toString()))
                .andExpect(jsonPath("$[0].doctorId").value(patient.getDoctor().getId()));
    }

    @Test
    void testFindById_PatientFound_ResultsInPatientBeingReturned() throws Exception {

        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor1")
                .lastName("DoctorLastName1")
                .specializations(Set.of(Specialization.PULMONOLOGY, Specialization.PEDIATRICS))
                .build();
        doctorRepository.save(doctor);

        Patient patient = Patient.builder()
                .id(1)
                .firstName("Patient1")
                .lastName("LastName1")
                .disease(Disease.BRONCHITIS)
                .doctor(doctor)
                .build();
        patientRepository.save(patient);

        mockMvc.perform(get("/api/v1/patients/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patient.getId()))
                .andExpect(jsonPath("$.firstName").value(patient.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(patient.getLastName()))
                .andExpect(jsonPath("$.disease").value(patient.getDisease().toString()))
                .andExpect(jsonPath("$.doctorId").value(patient.getDoctor().getId()));
    }
}
