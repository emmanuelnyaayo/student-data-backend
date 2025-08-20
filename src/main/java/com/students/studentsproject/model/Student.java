package com.students.studentsproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

    @Id
    private Long studentId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String className;
    private int score;
}
