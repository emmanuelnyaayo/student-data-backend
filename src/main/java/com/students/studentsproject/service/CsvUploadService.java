package com.students.studentsproject.service;

import com.students.studentsproject.util.TimeUtil;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CsvUploadService {

    private final DataSource dataSource;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public CsvUploadService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String uploadCsv(MultipartFile file) throws Exception {
        long startTime = System.currentTimeMillis();

        // Create a temp file with transformed data (score +5)
        File tempFile = File.createTempFile("students_processed", ".csv");
        int rowCount = preprocessCsv(file, tempFile);

        // Use COPY to bulk insert into Postgres
        try (Connection connection = dataSource.getConnection();
             Reader reader = new FileReader(tempFile);
             Statement stmt = connection.createStatement()) {
            
            // Start transaction
            connection.setAutoCommit(false);
            try {
                // Clear students table first
                stmt.execute("TRUNCATE students");

                // Bulk load into students directly
                CopyManager copyManager = new CopyManager((BaseConnection) connection.unwrap(BaseConnection.class));
                copyManager.copyIn(
                        "COPY students (student_id, first_name, last_name, dob, class_name, score) " +
                        "FROM STDIN WITH (FORMAT csv, HEADER true)", reader
                );

                connection.commit();
            } catch (Exception innerEx) {
                connection.rollback();
                throw innerEx;
            }
        }

        long endTime = System.currentTimeMillis();
        TimeUtil.logExecutionTime(startTime, endTime, "CSV Upload", rowCount);

        // Delete temp file
        tempFile.delete();

        return rowCount + " students uploaded successfully.";
    }

    private int preprocessCsv(MultipartFile inputFile, File outputFile) throws Exception {
        int rowCount = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFile.getInputStream()));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    // Write header unchanged
                    bw.write(line);
                    bw.newLine();
                    isHeader = false;
                    continue;
                }

                String[] cols = line.split(",");

                // Parse and adjust score (+5)
                int score = Integer.parseInt(cols[5].trim()) + 5;

                // Ensure proper formatting (especially date)
                LocalDate dob = LocalDate.parse(cols[3].trim(), DATE_FORMATTER);
                String formattedDob = dob.toString(); // Postgres expects YYYY-MM-DD

                // Rewrite line
                String transformedLine = String.join(",",
                        cols[0].trim(),  // student_id
                        cols[1].trim(),  // first_name
                        cols[2].trim(),  // last_name
                        formattedDob,    // dob (ISO format)
                        cols[4].trim(),  // class_name
                        String.valueOf(score) // adjusted score
                );

                bw.write(transformedLine);
                bw.newLine();
                rowCount++;
            }
        }

        return rowCount;
    }
}
