package com.students.studentsproject.service;

import com.github.pjfanning.xlsx.StreamingReader;
import com.students.studentsproject.util.TimeUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

@Service
public class ExcelToCsvService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public String convertExcelToCsv(MultipartFile file) throws Exception {
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(1000)   // cache 1000 rows
                .bufferSize(8192)     // 8KB read buffer
                .open(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);

        String baseDir = System.getProperty("os.name").toLowerCase().contains("win") ?
                "C:\\var\\log\\applications\\API\\dataprocessing\\" :
                "/var/log/applications/API/dataprocessing/";

        Files.createDirectories(Path.of(baseDir));
        String csvTimestamp = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String csvFilePath = baseDir + "students_csv_" + csvTimestamp + ".csv";

        long start = System.currentTimeMillis();
        int studentCount = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath), 1_048_576)) {
            for (Row row : sheet) {
                int cells = row.getLastCellNum();
                StringBuilder sb = new StringBuilder(cells * 8); // pre-size
                for (int i = 0; i < cells; i++) {
                    if (i > 0) sb.append(",");
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    if (row.getRowNum() > 0 && i == 5) {
                        int score = (int) cell.getNumericCellValue();
                        sb.append(score + 10);
                    } else {
                        sb.append(getCellValue(cell));
                    }
                }
                bw.write(sb.toString());
                bw.newLine();

                // Count only actual data rows, not header
                if (row.getRowNum() > 0) {
                    studentCount++;
                }
            }
        }

        workbook.close();

        long end = System.currentTimeMillis();
        TimeUtil.logExecutionTime(start, end, "Excel to CSV conversion", studentCount);

        return csvFilePath;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC: 
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Always force yyyy
                    return cell.getDateCellValue()
                            .toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .format(DATE_FORMAT);
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.valueOf((long) value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            case BLANK:   return "";
            default:      return "";
        }
    }

}
