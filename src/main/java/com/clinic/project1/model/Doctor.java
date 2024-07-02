package com.clinic.project1.model;


import com.clinic.project1.common.Specialization;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@SQLDelete(sql = "UPDATE student SET deleted = '1' WHERE id = ?")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "doctor_specialization", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "specialization")
    private Set<Specialization> specializations;

    @OneToMany(mappedBy = "doctor")
    private Set<Patient> patients;

    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments;
}
