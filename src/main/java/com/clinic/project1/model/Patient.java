package com.clinic.project1.model;


import com.clinic.project1.common.Disease;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@SQLDelete(sql = "UPDATE doctor SET deleted = '1' Where id = ?")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments;

    @ManyToOne
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private Disease disease;
}
