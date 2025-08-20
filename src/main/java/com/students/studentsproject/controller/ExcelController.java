package com.students.studentsproject.controller;
import org.springframework.web.bind.annotation.*;

import com.students.studentsproject.service.ExcelGeneratorService;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final ExcelGeneratorService excelService;

    public ExcelController(ExcelGeneratorService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/generate")
    public String generateExcel(@RequestParam int records) {
        try {
            String path = excelService.generateExcel(records);
            return "Excel file generated successfully: " + path;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating Excel: " + e.getMessage();
        }
    }
}
