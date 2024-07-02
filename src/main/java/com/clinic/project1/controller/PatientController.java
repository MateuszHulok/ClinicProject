package com.clinic.project1.controller;


import com.clinic.project1.model.command.create.CreatePatientCommand;
import com.clinic.project1.model.command.update.FullPatientUpdateCommand;
import com.clinic.project1.model.command.update.UpdatePatientDiseaseCommand;
import com.clinic.project1.model.dto.PatientDto;
import com.clinic.project1.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public List<PatientDto> findAll() {
        return patientService.findAll();
    }

    @PostMapping
    public PatientDto save(@RequestBody @Valid CreatePatientCommand command) {
        return patientService.save(command);
    }

    @GetMapping("/{id}")
    public PatientDto findById(@PathVariable int id) {
        return patientService.findById(id);
    }

    @PostMapping("/{id}")
    public PatientDto updatePatient(@PathVariable int id, @RequestBody @Valid FullPatientUpdateCommand command) {
        return patientService.update(id, command);
    }

    @PatchMapping("/{id}/disease")
    public PatientDto updatePatientDisease(@PathVariable int id, @RequestBody @Valid UpdatePatientDiseaseCommand command) {
        return patientService.updateDisease(id, command);
    }

    @DeleteMapping("/{id}")
    public void softDeletePatient(@PathVariable int id) {
        patientService.deleteById(id);
    }
}
