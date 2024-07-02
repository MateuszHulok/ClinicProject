package com.clinic.project1.model.command.update;


import com.clinic.project1.common.Disease;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePatientDiseaseCommand {

    @NotNull(message = "NULL_VALUE")
    private Disease disease;
}
