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
import com.clinic.project1.model.Appointment;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreateAppointmentCommand;
import com.clinic.project1.model.command.update.FullUpdateAppointmentCommand;
import com.clinic.project1.repository.AppointmentRepository;
import com.clinic.project1.repository.DoctorRepository;
import com.clinic.project1.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testFindAll_ResultsInAppointmentListBeingReturned() throws Exception {
        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor1")
                .lastName("DoctorLastName1")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .appointments(new HashSet<>())
                .build();
        doctorRepository.save(doctor);

        Patient patient = Patient.builder()
                .id(1)
                .firstName("Patient1")
                .lastName("LastName1")
                .build();
        patientRepository.save(patient);

        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        Appointment appointment = Appointment.builder()
                .id(1)
                .appointmentDate(appointmentDate)
                .doctor(doctor)
                .patient(patient)
                .build();
        appointmentRepository.save(appointment);

        String formattedDate = appointmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        mockMvc.perform(get("/api/v1/appointments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(appointment.getId()))
                .andExpect(jsonPath("$[0].appointmentDate").value(formattedDate))
                .andExpect(jsonPath("$[0].doctorId").value(appointment.getDoctor().getId()))
                .andExpect(jsonPath("$[0].patientId").value(appointment.getPatient().getId()));
    }

    @Test
    void testFindById_AppointmentFound_ResultsInAppointmentBeingReturned() throws Exception {
        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor1")
                .lastName("DoctorLastName1")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .appointments(Set.of())
                .build();
        doctorRepository.save(doctor);

        Patient patient = Patient.builder()
                .id(1)
                .firstName("Patient1")
                .lastName("LastName1")
                .build();
        patientRepository.save(patient);

        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        Appointment appointment = Appointment.builder()
                .id(1)
                .appointmentDate(appointmentDate)
                .doctor(doctor)
                .patient(patient)
                .build();
        appointmentRepository.save(appointment);

        String formattedDate = appointmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        mockMvc.perform(get("/api/v1/appointments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(appointment.getId()))
                .andExpect(jsonPath("$.appointmentDate").value(formattedDate))
                .andExpect(jsonPath("$.doctorId").value(appointment.getDoctor().getId()))
                .andExpect(jsonPath("$.patientId").value(appointment.getPatient().getId()));
    }

    @Test
    void testSave_NewAppointmentSaved_ResultsInAppointmentBeingSaved() throws Exception {
        Doctor doctor = Doctor.builder()
                .id(1)
                .firstName("Doctor1")
                .lastName("DoctorLastName1")
                .specializations(Set.of(Specialization.PULMONOLOGY))
                .appointments(Set.of())
                .build();
        doctorRepository.save(doctor);

        Patient patient = Patient.builder()
                .id(1)
                .firstName("Patient1")
                .lastName("LastName1")
                .build();
        patientRepository.save(patient);

        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        String formattedDate = appointmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        CreateAppointmentCommand command = CreateAppointmentCommand.builder()
                .doctorId(1)
                .patientId(1)
                .date(appointmentDate)
                .build();

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.appointmentDate").value(formattedDate))
                .andExpect(jsonPath("$.doctorId").value(command.getDoctorId()))
                .andExpect(jsonPath("$.patientId").value(command.getPatientId()));
    }
}