package com.students.studentsproject.service;

import com.students.studentsproject.util.TimeUtil; 
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class ExcelGeneratorService {

    private static final String[] CLASSES = {"Class1", "Class2", "Class3", "Class4", "Class5"};
    private static final Random RANDOM = new Random();
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public String generateExcel(int recordCount) throws Exception {
        long startTime = System.currentTimeMillis(); // ⏱ start benchmark

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(50)) {
            Sheet sheet = workbook.createSheet("Students");

            // Create header row
            Row header = sheet.createRow(0);
            String[] columns = {"studentId", "firstName", "lastName", "DOB", "class", "score"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Generate rows
            for (int i = 1; i <= recordCount; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(i); // studentId
                row.createCell(1).setCellValue(randomString(3, 8));
                row.createCell(2).setCellValue(randomString(3, 8));
                row.createCell(3).setCellValue(randomDob()); // dd-MM-yyyy
                row.createCell(4).setCellValue(CLASSES[RANDOM.nextInt(CLASSES.length)]);
                row.createCell(5).setCellValue(RANDOM.nextInt(21) + 55); // score 55–75
            }

            // Ensure directory exists
            String baseDir = System.getProperty("os.name").toLowerCase().contains("win") ?
                    "C:\\var\\log\\applications\\API\\dataprocessing\\" :
                    "/var/log/applications/API/dataprocessing/";
            Files.createDirectories(Path.of(baseDir));

            // Generate unique filename with timestamp
            String timestamp = java.time.LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filePath = baseDir + "students_excel_" + timestamp + ".xlsx";

            // Write to file
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }

            workbook.dispose();

            long endTime = System.currentTimeMillis(); // ⏱ end benchmark
            TimeUtil.logExecutionTime(startTime, endTime, "Excel Generation", recordCount);
            return filePath;
        }
    }

    // Optimized random string generator
    private String randomString(int min, int max) {
        int length = RANDOM.nextInt(max - min + 1) + min;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(chars);
    }

    // Optimized DOB generator (direct string, no LocalDate overhead)
    private String randomDob() {
        int year = 2000 + RANDOM.nextInt(11); // 2000–2010
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(
                java.time.YearMonth.of(year, month).lengthOfMonth()
        );
        return String.format("%02d-%02d-%04d", day, month, year); // dd-MM-yyyy
    }
}
