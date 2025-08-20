package com.students.studentsproject.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.students.studentsproject.model.Student;
import com.students.studentsproject.service.ExportService;
import com.students.studentsproject.service.StudentReportService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Iterator;

@RestController
@RequestMapping("/api/report")
public class StudentReportController {

    private final StudentReportService reportService;
    private final ExportService exportService;

    public StudentReportController(StudentReportService reportService, ExportService exportService) {
        this.reportService = reportService;
        this.exportService = exportService;
    }

    // Pagination
    @GetMapping("/students")
    public Page<Student> getStudents(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return reportService.getAllStudents(page, size);
    }

    // Search by ID
    @GetMapping("/students/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return reportService.getStudentById(id);
    }

    // Filter by class
    @GetMapping("/students/filter")
    public Page<Student> getStudentsByClass(@RequestParam String className,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return reportService.getStudentsByClass(className, page, size);
    }

    // Export CSV
    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=students.csv");

        Iterator<Student> students = reportService.streamAllForExport(); 
        try (PrintWriter writer = response.getWriter()) {
            // CSV header
            writer.println("studentId,firstName,lastName,dob,className,score");

            // Write each student row
            while (students.hasNext()) {
                Student s = students.next();
                writer.printf("%d,%s,%s,%s,%s,%d%n",
                        s.getStudentId(), s.getFirstName(), s.getLastName(),
                        s.getDob(), s.getClassName(), s.getScore());
            }
        }
    }


    // Export Excel
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        Iterator<Student> iterator = reportService.streamAllForExport();
        exportService.exportToExcel(response, iterator);
    }

    // Export PDF
    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        Iterator<Student> iterator = reportService.streamAllForExport();
        exportService.exportToPdf(response, iterator);  
    }
}
