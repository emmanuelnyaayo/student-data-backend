# StudentsBackend
## 📘 Student Data Backend (Spring Boot 3.5.4)

Spring Boot backend for managing student data with Excel/CSV processing, PostgreSQL bulk upload, and report export.

## 🚀 Features

Generate Excel datasets

Excel → CSV (+10 scores)

Upload CSV → DB (+5 scores)

Student reports (pagination, ID, class)

Export (Excel/PDF)

## ⚙️ Setup
git clone <backend-repo-link>
cd studentsproject
mvn spring-boot:run


Edit application.properties with PostgreSQL credentials.
👉 API: http://localhost:8080

## 📖 API

/api/excel/generate?records=100000

/api/excel/convert (Excel → CSV)

/api/csv/upload

/api/students?page=0&size=50

/api/export/excel | /api/export/pdf

## ⚡ Performance Notes

Excel → CSV may take 2–3 min (Apache POI limits).

For production: generate CSV directly or use faster native Excel parsers.