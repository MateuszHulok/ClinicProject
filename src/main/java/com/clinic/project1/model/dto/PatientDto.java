package com.clinic.project1.model.dto;

import com.clinic.project1.common.Disease;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PatientDto {
    private int id;
    private String firstName;
    private String lastName;
    private Disease disease;
    private int doctorId;
}
