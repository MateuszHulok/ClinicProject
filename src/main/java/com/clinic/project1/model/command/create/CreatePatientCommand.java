package com.clinic.project1.model.command.create;

import com.clinic.project1.common.Disease;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class CreatePatientCommand {

    @NotNull(message = "NULL_VALUE")
    @Pattern(regexp = "[A-Z][a-z]{1,19}", message = "PATTERN_MISMATCH {regexp}")
    private String firstName;

    @NotNull(message = "NULL_VALUE")
    @Pattern(regexp = "[A-Z][a-z]{1,29}", message = "PATTERN_MISMATCH {regexp}")
    private String lastName;

    @NotNull(message = "NULL_VALUE")
    private Disease disease;

    @Positive(message = "ILLEGAL_VALUE")
    private int doctorId;
}
