package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    private static Workbook workbook;
    private static Sheet sheet;

    public static void setExcelFile(String path, String sheetName) throws IOException {
        try (FileInputStream file = new FileInputStream(path)) {
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheet(sheetName);
        } catch (IOException e) {
            throw new IOException("Error opening Excel file: " + path, e);
        }
    }

    public static Object[][] getTestData(String path, String sheetName) throws IOException {
        setExcelFile(path, sheetName);

        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getLastCellNum();

        Object[][] data = new Object[rowCount - 1][colCount];

        for (int i = 1; i < rowCount; i++) { // Skip header
            Row row = sheet.getRow(i);
            for (int j = 0; j < colCount; j++) {
                data[i - 1][j] = row.getCell(j).getStringCellValue();
            }
        }
        return data;
    }

    public static void writeValidCredentials(String username, String password, String filePath) {
        File file = new File(filePath);
        try {
            // Ensure the parent directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                Files.createDirectories(Paths.get(parentDir.getAbsolutePath()));
                System.out.println("Created directory: " + parentDir.getAbsolutePath());
            }

            Workbook workbook;
            Sheet sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                    sheet = workbook.getSheetAt(0);
                }
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("ValidLogin");

                // Create header row if file is new
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Username");
                headerRow.createCell(1).setCellValue("Password");
            }

            int rowCount = sheet.getPhysicalNumberOfRows();
            Row dataRow = sheet.createRow(rowCount);
            dataRow.createCell(0).setCellValue(username);
            dataRow.createCell(1).setCellValue(password);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            System.out.println("Valid credentials written to Excel successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing credentials to Excel.");
        }
    }
}
