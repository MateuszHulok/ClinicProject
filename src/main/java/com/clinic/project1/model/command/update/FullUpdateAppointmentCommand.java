package com.clinic.project1.model.command.update;


import jakarta.validation.constraints.Future;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FullUpdateAppointmentCommand {

    @Future(message = "DATE_NOT_FUTURE")
    private LocalDateTime appointmentDate;
    private int doctorId;
    private int patientId;
}
