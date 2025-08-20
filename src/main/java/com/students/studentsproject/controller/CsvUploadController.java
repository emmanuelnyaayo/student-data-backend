package com.students.studentsproject.controller;
import com.students.studentsproject.service.CsvUploadService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class CsvUploadController {

    private final CsvUploadService csvUploadService;

    public CsvUploadController(CsvUploadService csvUploadService) {
        this.csvUploadService = csvUploadService;
    }

    @PostMapping("/csv-to-db")
    public String uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            return csvUploadService.uploadCsv(file);
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error uploading CSV: " + e.getMessage();
        }
    }
}

