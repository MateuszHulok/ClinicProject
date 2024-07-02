package com.clinic.project1.controller;


import com.clinic.project1.model.command.create.CreateAppointmentCommand;
import com.clinic.project1.model.command.update.FullUpdateAppointmentCommand;
import com.clinic.project1.model.dto.AppointmentDto;
import com.clinic.project1.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;


    @GetMapping
    public List<AppointmentDto> findAll() {
        return appointmentService.findAll();
    }

    @PostMapping()
    public ResponseEntity<AppointmentDto> save(@RequestBody @Valid CreateAppointmentCommand command) {
        AppointmentDto createdAppointment = appointmentService.save(command);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public AppointmentDto findById(@PathVariable int id) {
        return appointmentService.findById(id);
    }

    @PutMapping("/{id}/date")
    public AppointmentDto updateAppointment(@PathVariable int id, @RequestBody @Valid FullUpdateAppointmentCommand command) {
        return appointmentService.update(id, command);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable int id) {
        appointmentService.deleteById(id);
    }
}
