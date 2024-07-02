package com.clinic.project1.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentDto {
    private int id;
    private LocalDateTime appointmentDate;
    private int doctorId;
    private int patientId;
}
