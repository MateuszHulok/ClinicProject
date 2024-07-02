package com.clinic.project1.model.dto;

import com.clinic.project1.common.Specialization;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class DoctorDto {
    private int id;
    private String firstName;
    private String lastName;
    private Set<Specialization> specializations;
}
