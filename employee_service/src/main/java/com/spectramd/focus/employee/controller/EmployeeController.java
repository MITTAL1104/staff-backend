package com.spectramd.focus.employee.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spectramd.focus.employee.entity.Employee;
import com.spectramd.focus.employee.service.EmployeeService;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import com.spectramd.focus.employee.service.EmployeeExcelExportService;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    @Autowired
    private final EmployeeService employeeService;

    @Autowired
    private final EmployeeExcelExportService employeeExcelExportService;

    public EmployeeController(EmployeeService employeeService, EmployeeExcelExportService employeeExcelExportService) {
        this.employeeService = employeeService;
        this.employeeExcelExportService = employeeExcelExportService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/getAllActive")
    public ResponseEntity<List<Employee>> getAllActiveEmployees() {
        return ResponseEntity.ok(employeeService.getAllActiveEmployees());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/getAllByName/{name}")
    public ResponseEntity<List<Employee>> getEmployeeByName(@PathVariable String name) {
        return ResponseEntity.ok(employeeService.getEmployeeByName(name));
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<Employee> getEmployeeByNameForUpdate(@PathVariable String name) {
        return ResponseEntity.ok(employeeService.getEmployeeByNameForUpdate(name));
    }

    @GetMapping("/getNames/{name}")
    public ResponseEntity<List<String>> getEmployeeNames(@PathVariable String name) {
        return ResponseEntity.ok(employeeService.getEmployeeNames(name));
    }

    @GetMapping("/getAllNames")
    public ResponseEntity<List<Map<String, Object>>> getAllEmpNames() {
        return ResponseEntity.ok(employeeService.getAllEmpNames());
    }

    @GetMapping("/getIds/{name}")
    public ResponseEntity<List<Integer>> getEmployeeIds(@PathVariable String name) {
        return ResponseEntity.ok(employeeService.getEmployeeIds(name));
    }

    @GetMapping("/getNameByEmail/{email}")
    public ResponseEntity<String> getEmployeeNameByEmail(@PathVariable String email) {
        return ResponseEntity.ok(employeeService.getEmployeeNameByEmail(email));
    }

    @GetMapping("/getRoles")
    public ResponseEntity<List<Map<String, Object>>> fetchAllRoles() {
        return ResponseEntity.ok(employeeService.fetchAllRoles());
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return ResponseEntity.ok("Employee Added Successfully");
    }

    @PutMapping("/updateId/{id}")
    public ResponseEntity<String> updateEmployeeById(@PathVariable int id, @RequestBody Employee employee) {

        if (!employeeService.employeeIdExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee with ID " + id + " not found!");
        }

        if (employeeService.checkDateOfJoiningConflict(id, employee.getDateOfJoining())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Date of Joining conflicting with active allocation");
        }

        if (employeeService.empIdAllocationExists(id) && !employee.getIsActive()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Employee has active allocations!");
        }

        employeeService.updateEmployeeById(employee);
        return ResponseEntity.ok("Employee updated successfully");
    }

    @PutMapping("/updateName/{name}")
    public ResponseEntity<String> updateEmployeeByName(@PathVariable String name, @RequestBody Employee employee) {

        if (!employeeService.employeeNameExists(name)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee with Name " + name + " not found!");
        }

        employeeService.updateEmployeeByName(employee);
        return ResponseEntity.ok("Employee updated successfully");
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllEmployees() {

        if (!employeeService.employeeExists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No Employee found!");
        }

        employeeService.deleteAllEmployees();
        return ResponseEntity.ok("All Employees Deleted Successfully");
    }

    @DeleteMapping("/deleteId/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable int id) {
        try {

            if (!employeeService.employeeIdExists(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee with ID '" + id + "' not found!");
            }

            if (!employeeService.empIdIsActiveCheck(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Employee with ID '" + id + "' is already inactive!");
            }

            if (employeeService.empIdAllocationExists(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Employee with ID '" + id + "' has active allocations!");
            }

            if (employeeService.checkActiveProjectOwner(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Employee with ID '" + id + "' is owner of an active project!");
            }

            employeeService.deleteEmployeeById(id);
            return ResponseEntity.ok("Employee Deleted Successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteName/{name}")
    public ResponseEntity<String> deleteEmployeeByName(@PathVariable String name) {

        int id = employeeService.getEmpIdByName(name);

        if (!employeeService.employeeNameExists(name)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee with Name '" + name + "' not found!");
        }

        if (!employeeService.empNameIsActiveCheck(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Employee with Name '" + name + "' is already inactive!");
        }

        if (employeeService.empNameAllocationExists(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Employee with Name '" + name + "' has active allocations!");
        }

        if (employeeService.checkActiveProjectOwner(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Employee with Name '" + name + "' is owner of an active project");
        }

        employeeService.deleteEmployeeByName(name);
        return ResponseEntity.ok("Employee Deleted Successfully");
    }

    @GetMapping("/downloadExcel")
    public void downloadEmployeeExcel(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value,
            HttpServletResponse response) {

        System.out.println("Controller Thread: " + Thread.currentThread().getName());

        List<Employee> employees;

        if ("byId".equalsIgnoreCase(type) && value != null) {
            Employee emp = employeeService.getEmployeeById(Integer.parseInt(value));
            employees = emp != null ? List.of(emp) : new ArrayList<>();
        } else if ("byName".equalsIgnoreCase(type) && value != null) {
            employees = employeeService.getEmployeeByName(value);
        } else if ("allActive".equalsIgnoreCase(type)) {
            employees = employeeService.getAllActiveEmployees();
        } else {
            employees = employeeService.getAllEmployees();
        }

        employeeExcelExportService.generateExcelAsync(employees, response);
    }
}
