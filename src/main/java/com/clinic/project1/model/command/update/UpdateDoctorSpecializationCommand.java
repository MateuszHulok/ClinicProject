package com.clinic.project1.model.command.update;


import com.clinic.project1.common.Specialization;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateDoctorSpecializationCommand {

    @NotEmpty(message = "EMPTY_VALUE")
    private Set<Specialization> specializations;
}
