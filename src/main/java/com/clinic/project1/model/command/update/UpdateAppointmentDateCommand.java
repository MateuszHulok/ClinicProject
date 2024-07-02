package com.clinic.project1.model.command.update;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateAppointmentDateCommand {

    @NotNull
    @Future(message = "DATE_NOT_FUTURE")
    private LocalDateTime dateTime;
}
