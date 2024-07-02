package com.clinic.project1.mapper;

import com.clinic.project1.model.Appointment;
import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreateAppointmentCommand;
import com.clinic.project1.model.dto.AppointmentDto;

public class AppointmentMapper {
    public static AppointmentDto mapToDto(Appointment appointment) {
        return AppointmentDto.builder()
                .id(appointment.getId())
                .appointmentDate(appointment.getAppointmentDate())
                .doctorId(appointment.getDoctor().getId())
                .patientId(appointment.getPatient().getId())
                .build();
    }
}

