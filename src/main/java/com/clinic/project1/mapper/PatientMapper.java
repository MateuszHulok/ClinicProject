package com.clinic.project1.mapper;

import com.clinic.project1.model.Doctor;
import com.clinic.project1.model.Patient;
import com.clinic.project1.model.command.create.CreatePatientCommand;
import com.clinic.project1.model.dto.PatientDto;

public class PatientMapper {
    public static PatientDto mapToDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .disease(patient.getDisease())
                .doctorId(patient.getDoctor().getId())
                .build();
    }

    public static Patient mapFromCommand(CreatePatientCommand command, Doctor doctor) {
        return Patient.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .disease(command.getDisease())
                .doctor(doctor)
                .build();
    }
}
