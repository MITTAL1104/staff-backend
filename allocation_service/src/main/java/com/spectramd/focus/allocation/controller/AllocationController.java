/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.allocation.controller;

import com.spectramd.focus.allocation.entity.Allocation;
import com.spectramd.focus.allocation.service.AllocationExcelExportService;
import com.spectramd.focus.allocation.service.AllocationService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author raghav.mittal
 */
@RestController
@RequestMapping("/allocation")
public class AllocationController {

    @Autowired
    private final AllocationService allocationService;

    @Autowired
    private final AllocationExcelExportService allocationExcelExportService;

    public AllocationController(AllocationService allocationService, AllocationExcelExportService allocationExcelExportService) {
        this.allocationService = allocationService;
        this.allocationExcelExportService = allocationExcelExportService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Allocation>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    @GetMapping("/getAllActive")
    public ResponseEntity<List<Allocation>> getAllActiveAllocations() {
        return ResponseEntity.ok(allocationService.getAllActiveAllocations());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Allocation> getAllocationById(@PathVariable int id) {

        return ResponseEntity.ok(allocationService.getByAllocationId(id));
    }

    @GetMapping("/getAllocByEmpName/{name}")
    public ResponseEntity<List<Allocation>> getAllAllocationByEmployeeName(@PathVariable String name) {

        return ResponseEntity.ok(allocationService.getAllAllocationByEmployeeName(name));
    }

    @GetMapping("/getByEmpName/{name}")
    public ResponseEntity<Allocation> getAllocationByEmployeeName(@PathVariable String name) {

        return ResponseEntity.ok(allocationService.getAllocationByEmployeeName(name));
    }

    @GetMapping("/getAllocDelByEmpName/{name}")
    public ResponseEntity<List<Allocation>> getAllocationForDeleteByEmployeeName(@PathVariable String name) {

        return ResponseEntity.ok(allocationService.getAllocForDeleteByEmpName(name));
    }

    @GetMapping("/getAllocDelByProjName/{name}")
    public ResponseEntity<List<Allocation>> getAllocationForDeleteByProjectName(@PathVariable String name) {

        return ResponseEntity.ok(allocationService.getAllocForDeleteByProjName(name));
    }

    @GetMapping("/updateByProjName/{name}")
    public ResponseEntity<Allocation> getSingleAllocationByProjectName(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getSingleAllocationByProjectName(name));
    }

    @GetMapping("/getByProjName/{name}")
    public ResponseEntity<List<Allocation>> getAllocationByProjectName(@PathVariable String name) {

        return ResponseEntity.ok(allocationService.getAllocationByProjectName(name));
    }

    @GetMapping("/getByEmpId/{id}")
    public ResponseEntity<List<Allocation>> getAllocationByEmployeeId(@PathVariable int id) {

        return ResponseEntity.ok(allocationService.getByEmployeeId(id));
    }

    @GetMapping("/getByProjId/{id}")
    public ResponseEntity<List<Allocation>> getAllocationByProjectId(@PathVariable int id) {

        return ResponseEntity.ok(allocationService.getByProjectId(id));
    }

    @GetMapping("/getProjIdByEmpId/{id}")
    public ResponseEntity<Integer> getProjIdByEmpId(@PathVariable Integer id) {
        return ResponseEntity.ok(allocationService.getProjIdByEmpId(id));
    }

    @GetMapping("/getNames/{name}")
    public ResponseEntity<List<String>> getAllocatedEmployeeNames(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getAllocatedEmployeeNames(name));
    }

    @GetMapping("/getProjects/{name}")
    public ResponseEntity<List<String>> getAllocatedProjectNames(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getAllocatedProjectNames(name));
    }

    @GetMapping("/getAllocIds/{name}")
    public ResponseEntity<List<Integer>> getAllocatedIds(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getAllocatedIds(name));
    }

    @GetMapping("/getIds/{name}")
    public ResponseEntity<List<Integer>> getAllocatedEmployeeIds(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getAllocatedEmployeeIds(name));
    }

    @GetMapping("/getEmpIdByName/{name}")
    public ResponseEntity<Integer> getEmpIdByName(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getEmpIdByName(name));
    }

    @GetMapping("/getProjIdByName/{name}")
    public ResponseEntity<Integer> getProjIdByName(@PathVariable String name) {
        return ResponseEntity.ok(allocationService.getProjIdByName(name));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addAllocation(@RequestBody Allocation allocation) {
        try {

            int assigneeId = allocationService.getEmpIdByName(allocation.getAssigneeName());
            int projectId = allocationService.getProjIdByName(allocation.getProjectName());

            System.out.println("Assignee ID: " + assigneeId);
            System.out.println("Project ID: " + projectId);

            if (assigneeId == -1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Employee with name '" + allocation.getAssigneeName() + "'not found:");
            }

            if (!allocationService.employeeIsActiveCheck(assigneeId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Employee with name '" + allocation.getAssigneeName() + "' is inactive!");
            }

            if (projectId == -1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Project with name '" + allocation.getProjectName() + "'not found:");
            }

            if (!allocationService.projectIsActiveCheck(projectId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Project with name '" + allocation.getProjectName() + "' is inactive!");
            }

            allocation.setAssigneeId(assigneeId);
            allocation.setProjectId(projectId);

            String allocationStartDate = allocation.getAllocationStartDate();
            String allocationEndDate = allocation.getAllocationEndDate();

            if (!allocationService.empJoiningDateCheck(allocationStartDate, assigneeId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Allocation start date cannot be before employee joining date");
            }

            if (!allocationService.projectDatesCheck(allocationStartDate, allocationEndDate, projectId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Allocation dates should be within the project start and end dates");
            }

            if (allocationService.datesOverlapCheckForActiveAllocation(assigneeId, allocationStartDate, allocationEndDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("An active allocation already exists with overlapping dates for this employee");
            }

            if (allocationService.isAllocationExistsForSameProject(assigneeId, projectId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Employee already allocated to the project");
            }

            allocationService.addAllocation(allocation);
            return ResponseEntity.ok("Allocation Added Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding allocation: " + e.getMessage());
        }
    }

    @PutMapping("/updateId/{id}")
    public ResponseEntity<String> updateAllocation(@PathVariable int id, @RequestBody Allocation allocation) {

        if (!allocationService.allocationIdExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Allocation with ID " + id + " not found!");
        }

        if (!allocationService.empJoiningDateCheck(allocation.getAllocationStartDate(), allocation.getAssigneeId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Allocation start date cannot be before employee joining date");
        }

        if (!allocationService.projectDatesCheck(allocation.getAllocationStartDate(),allocation.getAllocationEndDate(),allocation.getProjectId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Allocation dates should be within the project start and end dates");
        }

        if (allocationService.datesOverlapCheckForActiveAllocation(allocation.getAssigneeId(), allocation.getAllocationStartDate(), allocation.getAllocationStartDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An active allocation already exists with overlapping dates for this employee");
        }

        allocation.setAllocationId(id);

        allocationService.updateAllocation(allocation);
        return ResponseEntity.ok("Allocation updated successfully");
    }

    @PutMapping("/updateAllocByProjId/{id}")
    public ResponseEntity<String> updateAllocationByProjId(@PathVariable int id, @RequestBody Allocation allocation) {

        allocation.setAllocationId(id);
        allocationService.updateAllocationByProjId(allocation);
        return ResponseEntity.ok("Allocation updated successfully");
    }

    @DeleteMapping("deleteAll")
    public ResponseEntity<String> deleteAllAllocations() {

        allocationService.deleteAllAllocations();
        return ResponseEntity.ok("All Allocations Deleted Successfully");
    }

    @DeleteMapping("deleteId/{id}")
    public ResponseEntity<String> deleteAllocationById(@PathVariable int id) {

        if (!allocationService.allocationIdExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Allocation with ID '" + id + "' not found!");
        }

        allocationService.deleteByAllocationId(id);
        return ResponseEntity.ok("Allocation Deleted Successfully");
    }

    @DeleteMapping("deleteEmpId/{id}")
    public ResponseEntity<String> deleteAllocationByEmployeeId(@PathVariable int id) {

        if (!allocationService.allocationAssigneeIdExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Allocation for Employee ID '" + id + "' not found!");
        }

        allocationService.deleteByEmployeeId(id);
        return ResponseEntity.ok("Allocation Deleted Successfully");
    }

    @DeleteMapping("deleteProjId/{id}")
    public ResponseEntity<String> deleteAllocationByProjectId(@PathVariable int id) {

        if (!allocationService.allocationProjectIdExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Allocations for Project ID '" + id + "' not found!");
        }

        allocationService.deleteByProjectId(id);
        return ResponseEntity.ok("Allocation Deleted Successfully");
    }

    @GetMapping("/downloadExcel")
    public void downloadEmployeeExcel(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value,
            HttpServletResponse response) {

        System.out.println("Controller Thread: " + Thread.currentThread().getName());

        List<Allocation> allocations;

        if ("byId".equalsIgnoreCase(type) && value != null) {
            Allocation alloc = allocationService.getByAllocationId(Integer.parseInt(value));
            allocations = alloc != null ? List.of(alloc) : new ArrayList<>();
        } else if ("byEmployeeName".equalsIgnoreCase(type) && value != null) {
            allocations = allocationService.getAllAllocationByEmployeeName(value);
        } else if ("byProjectName".equalsIgnoreCase(type) && value != null) {
            allocations = allocationService.getAllocationByProjectName(value);
        } else if ("allActive".equalsIgnoreCase(type)) {
            allocations = allocationService.getAllActiveAllocations();
        } else {
            allocations = allocationService.getAllAllocations();
        }

        allocationExcelExportService.generateExcelAsync(allocations, response);
    }

}
