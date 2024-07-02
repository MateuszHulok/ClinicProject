package com.clinic.project1.model.command.create;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateAppointmentCommand {

    @NotNull
    @Future(message = "DATE_NOT_FUTURE")
    private LocalDateTime date;

    @Positive(message = "ILLEGAL_VALUE")
    private int doctorId;

    @Positive(message = "ILLEGAL_VALUE")
    private int patientId;
}
