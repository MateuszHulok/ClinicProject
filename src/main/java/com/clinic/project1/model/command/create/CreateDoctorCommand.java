package com.clinic.project1.model.command.create;

import com.clinic.project1.common.Specialization;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CreateDoctorCommand {

    @NotNull(message = "NULL_VALUE")
    @Pattern(regexp = "[A-Z][a-z]{1,19}", message = "PATTERN_MISMATCH {regexp}")
    private String firstName;

    @NotNull(message = "NULL_VALUE")
    @Pattern(regexp = "[A-Z][a-z]{1,29}", message = "PATTERN_MISMATCH {regexp}")
    private String lastName;

    @NotEmpty(message = "EMPTY_VALUE")
    private Set<Specialization> specializationSet;
}