package com.clinic.project1.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clinic.project1.common.Specialization;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.command.create.CreateDoctorCommand;
import com.clinic.project1.model.command.update.FullUpdateDoctorCommand;
import com.clinic.project1.repository.DoctorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testSave_NewDoctorSaved_ResultsInDoctorBeingSaved() throws Exception {
        CreateDoctorCommand command = CreateDoctorCommand.builder()
                .firstName("New")
                .lastName("Doctor")
                .specializationSet(Set.of(Specialization.PULMONOLOGY))
                .build();

        mockMvc.perform(post("/api/v1/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(command.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(command.getLastName()))
                .andExpect(jsonPath("$.specializations", containsInAnyOrder(Specialization.PULMONOLOGY.toString())));
    }

    @Test
    void testUpdate_DoctorUpdated_ResultsInDoctorBeingUpdated() throws Exception {
        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Test")
                .lastName("Testowy")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .appointments(new HashSet<>())
                .build();
        doctorRepository.save(doctor);

        FullUpdateDoctorCommand command = FullUpdateDoctorCommand.builder()
                .firstName("Updated")
                .lastName("Doctor")
                .specializations(Set.of(Specialization.PEDIATRICS))
                .build();

        mockMvc.perform(put("/api/v1/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctor.getId()))
                .andExpect(jsonPath("$.firstName").value(command.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(command.getLastName()))
                .andExpect(jsonPath("$.specializations", containsInAnyOrder(Specialization.PEDIATRICS.toString())));
    }
}

