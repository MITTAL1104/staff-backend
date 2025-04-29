/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.employee.service;

import com.spectramd.focus.employee.entity.Employee;
import java.io.IOException;
import static java.lang.Boolean.TRUE;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author raghav.mittal
 */
@Service
public class EmployeeExcelExportServiceImpl implements EmployeeExcelExportService {

    @Async("taskExecutor")
    @Override
    public void generateExcelAsync(List<Employee> employees, HttpServletResponse response) {
        System.out.println("Excel generation running in: " + Thread.currentThread().getName());

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Employee");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Employee ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Date of Joining");
            header.createCell(4).setCellValue("Role");
            header.createCell(5).setCellValue("Status");
            header.createCell(6).setCellValue("Admin");

            int rowIdx = 1;
            for (Employee emp : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(emp.getEmployeeId());
                row.createCell(1).setCellValue(emp.getName());
                row.createCell(2).setCellValue(emp.getEmail());
                row.createCell(3).setCellValue(emp.getDateOfJoining());
                row.createCell(4).setCellValue(emp.getRoleName());
                row.createCell(5).setCellValue(emp.getIsActive() == TRUE ? "Active" : "Inactive");
                row.createCell(6).setCellValue(emp.getIsAdmin() == TRUE ? "Yes" : "No");

            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel", e);
        }
    }
    
}
