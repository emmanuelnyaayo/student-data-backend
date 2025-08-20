package com.students.studentsproject.repository;

import com.students.studentsproject.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findByClassName(String className, Pageable pageable);
}


    
