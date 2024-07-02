package com.clinic.project1.mapper;

import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.command.create.CreateDoctorCommand;
import com.clinic.project1.model.dto.DoctorDto;

public class DoctorMapper {

    public static DoctorDto mapToDto(Doctor doctor) {
        return DoctorDto.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specializations(doctor.getSpecializations())
                .build();
    }

    public static Doctor mapFromCommand(CreateDoctorCommand command) {
        return Doctor.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .specializations(command.getSpecializationSet())
                .build();
    }
}

