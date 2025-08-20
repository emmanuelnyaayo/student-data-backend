package com.students.studentsproject.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.students.studentsproject.service.ExcelToCsvService;

@RestController
@RequestMapping("/api/process")
public class ExcelToCsvController {

    private final ExcelToCsvService excelToCsvService;

    public ExcelToCsvController(ExcelToCsvService excelToCsvService) {
        this.excelToCsvService = excelToCsvService;
    }

    @PostMapping("/excel-to-csv")
    public String uploadExcelAndConvert(@RequestParam("file") MultipartFile file) {
        try {
            String path = excelToCsvService.convertExcelToCsv(file);
            return "CSV file generated successfully: " + path;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error processing Excel file: " + e.getMessage();
        }
    }
}

