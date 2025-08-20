package com.students.studentsproject.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.students.studentsproject.model.Student;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.util.Iterator;

@Service
public class ExportService {

    // STREAMING EXCEL EXPORT
    public void exportToExcel(HttpServletResponse response, Iterator<Student> studentIterator) throws Exception {
        Workbook workbook = new SXSSFWorkbook(100); 
        Sheet sheet = workbook.createSheet("Students");

        // Header
        Row header = sheet.createRow(0);
        String[] columns = {"studentId","firstName","lastName","dob","className","score"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        while (studentIterator.hasNext()) {
            Student s = studentIterator.next();
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getStudentId());
            row.createCell(1).setCellValue(s.getFirstName());
            row.createCell(2).setCellValue(s.getLastName());
            row.createCell(3).setCellValue(s.getDob().toString());
            row.createCell(4).setCellValue(s.getClassName());
            row.createCell(5).setCellValue(s.getScore());
        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=students.xlsx");
        try (OutputStream os = response.getOutputStream()) {
            workbook.write(os);
        }
        ((SXSSFWorkbook) workbook).dispose(); 
        workbook.close();
    }

    // STREAMING PDF EXPORT
    public void exportToPdf(HttpServletResponse response, Iterator<Student> studentIterator) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=students.pdf");

        Document document = new Document(PageSize.A4.rotate()); 
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.add(new Paragraph("Student Report"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 3f, 3f, 3f, 2f, 2f});

        // Header row
        table.addCell("studentId");
        table.addCell("firstName");
        table.addCell("lastName");
        table.addCell("dob");
        table.addCell("className");
        table.addCell("score");

        int batch = 0;
        while (studentIterator.hasNext()) {
            Student s = studentIterator.next();
            table.addCell(s.getStudentId().toString());
            table.addCell(s.getFirstName());
            table.addCell(s.getLastName());
            table.addCell(s.getDob().toString());
            table.addCell(s.getClassName());
            table.addCell(String.valueOf(s.getScore()));

            batch++;
            if (batch % 1000 == 0) { // flush periodically
                document.add(table);
                table.deleteBodyRows(); // free memory
            }
        }

        if (table.getRows().size() > 1) { // remaining rows
            document.add(table);
        }

        document.close();
    }
}
