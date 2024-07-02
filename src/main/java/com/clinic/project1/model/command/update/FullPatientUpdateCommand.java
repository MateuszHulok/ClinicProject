package com.clinic.project1.model.command.update;


import com.clinic.project1.common.Disease;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FullPatientUpdateCommand {


    @Pattern(regexp = "[A-Z][a-z]{1,19}", message = "PATTERN_MISMATCH {regexp}")
    private String firstName;

    @Pattern(regexp = "[A-Z][a-z]{1,19}", message = "PATTERN_MISMATCH {regexp}")
    private String lastName;

    private Disease disease;
    private int doctorId;
}
