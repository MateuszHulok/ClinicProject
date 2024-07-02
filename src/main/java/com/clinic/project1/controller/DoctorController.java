package com.clinic.project1.controller;


import com.clinic.project1.model.command.create.CreateDoctorCommand;
import com.clinic.project1.model.command.update.FullUpdateDoctorCommand;
import com.clinic.project1.model.command.update.UpdateDoctorSpecializationCommand;
import com.clinic.project1.model.dto.DoctorDto;
import com.clinic.project1.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorDto> findAll() {
        return doctorService.findAll();
    }


    @PostMapping
    public ResponseEntity<DoctorDto> save(@RequestBody @Valid CreateDoctorCommand command) {
        DoctorDto doctorDto = doctorService.save(command);
        return new ResponseEntity<>(doctorDto, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public DoctorDto findById(@PathVariable int id) {
        return doctorService.findById(id);
    }


    @PutMapping("/{id}")
    public DoctorDto updateDoctor(@PathVariable int id, @RequestBody @Valid FullUpdateDoctorCommand command) {
        return doctorService.update(id, command);
    }


    @PatchMapping("/{id}/specializations")
    public DoctorDto updateDoctorSpecialization(@PathVariable int id, @RequestBody @Valid UpdateDoctorSpecializationCommand command) {
        return doctorService.updateSpecialization(id, command);
    }

    @DeleteMapping("/{id}")
    public void softDeleteDoctor(@PathVariable int id) {
        doctorService.deleteById(id);
    }
}
