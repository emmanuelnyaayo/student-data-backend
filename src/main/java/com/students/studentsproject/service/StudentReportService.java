package com.students.studentsproject.service;

import com.students.studentsproject.model.Student;
import com.students.studentsproject.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.stream.Stream;


@Service
public class StudentReportService {

    private final StudentRepository studentRepository;

    public StudentReportService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> getAllStudents(int page, int size) {
        return studentRepository.findAll(PageRequest.of(page, size));
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Page<Student> getStudentsByClass(String className, int page, int size) {
        return studentRepository.findByClassName(className, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Iterator<Student> streamAllForExport() {
        Stream<Student> studentStream = studentRepository.findAll().stream(); 
        return studentStream.iterator();
    }
}
