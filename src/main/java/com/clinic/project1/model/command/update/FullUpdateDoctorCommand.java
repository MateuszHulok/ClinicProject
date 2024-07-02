package com.clinic.project1.model.command.update;


import com.clinic.project1.common.Specialization;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FullUpdateDoctorCommand {


    @Pattern(regexp = "[A-Z][a-z]{1,19}", message = "PATTERN_MISSMATCH {regexp}")
    private String firstName;

    @Pattern(regexp = "[A-Z][a-z]{1,29}", message = "PATTERN_MISSMATCH {regexp}")
    private String lastName;

    private Set<Specialization> specializations;
}
