package com.spectramd.focus.project.controller;

import com.spectramd.focus.project.entity.Project;
import com.spectramd.focus.project.service.ProjectExcelExportService;
import com.spectramd.focus.project.service.ProjectService;
import static java.lang.Boolean.FALSE;
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

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private final ProjectService projectService;

    @Autowired
    private final ProjectExcelExportService projectExcelExportService;

    public ProjectController(ProjectService projectService, ProjectExcelExportService projectExcelExportService) {
        this.projectService = projectService;
        this.projectExcelExportService = projectExcelExportService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/getAllActive")
    public ResponseEntity<List<Project>> getAllActiveProjects() {
        return ResponseEntity.ok(projectService.getAllActiveProjects());
    }

    @GetMapping("getById/{id}")
    public ResponseEntity<Project> geProjectById(@PathVariable int id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("getAllByName/{name}")
    public ResponseEntity<List<Project>> getProjectByName(@PathVariable String name) {
        return ResponseEntity.ok(projectService.getProjectByName(name));
    }

    @GetMapping("getByName/{name}")
    public ResponseEntity<Project> getProjectByNameForUpdate(@PathVariable String name) {
        return ResponseEntity.ok(projectService.getProjectByNameForUpdate(name));
    }

    @GetMapping("/getNames/{name}")
    public ResponseEntity<List<String>> getProjectNames(@PathVariable String name) {
        return ResponseEntity.ok(projectService.getProjectNames(name));
    }

    @GetMapping("/getIds/{name}")
    public ResponseEntity<List<Integer>> getProjectIds(@PathVariable String name) {
        return ResponseEntity.ok(projectService.getProjectIds(name));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProject(@RequestBody Project project) {

        projectService.addProject(project);
        return ResponseEntity.ok("Project Added Successfully");
    }

    @PutMapping("/updateId/{id}")
    public ResponseEntity<String> updateProject(@PathVariable int id, @RequestBody Project project) {

        if (!projectService.projectIdExists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project with ID " + id + " not found!");
        }

        if (projectService.checkProjStartDateConflict(id, project.getStartDate())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Project Start Date conflicting with active allocations");
        }

        if (projectService.checkProjEndDateConflict(id, project.getEndDate())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Project End Date conflicting with active allocations");
        }

        if (projectService.projIdAllocationExists(id) && !project.getIsActive()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Project has active allocations!");
        }

        projectService.updateProjectById(project);
        return ResponseEntity.ok("Project updated successfully");
    }

    @PutMapping("/updateName/{name}")
    public ResponseEntity<String> updateProjectByName(@PathVariable String name, @RequestBody Project project) {

        if (!projectService.projectNameExists(name)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project with name " + name + " not found!");

        }

        projectService.updateProjectByName(project);
        return ResponseEntity.ok("Project updated successfully");
    }

    @DeleteMapping("deleteAll")
    public ResponseEntity<String> deleteAllProjects() {

        if (!projectService.projectExists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No Project found!");
        }

        projectService.deleteAllProjects();
        return ResponseEntity.ok("All Projects Deleted Successfully");
    }

    @DeleteMapping("deleteId/{id}")
    public ResponseEntity<String> deleteProjectById(@PathVariable int id) {
        try {
            if (!projectService.projectIdExists(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Project with ID '" + id + "' not found!");
            }

            if (!projectService.projIdIsActiveCheck(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Project with ID '" + id + "' is already inactive!");
            }

            if (projectService.projIdAllocationExists(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Project with ID '" + id + "' has active allocations!");
            }

            projectService.deleteProjectById(id);
            return ResponseEntity.ok("Project Deleted Successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("deleteName/{name}")
    public ResponseEntity<String> deleteProjectByName(@PathVariable String name) {

        if (!projectService.projectNameExists(name)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project with Name '" + name + "' not found!");
        }

        if (!projectService.projNameIsActiveCheck(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Project with Name '" + name + "' is already inactive!");
        }

        if (projectService.projNameAllocationExists(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Project with Name '" + name + "' has active allocations!");
        }

        projectService.deleteProjectByName(name);
        return ResponseEntity.ok("Project Deleted Successfully");
    }

    @GetMapping("/downloadExcel")
    public void downloadEmployeeExcel(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value,
            HttpServletResponse response) {

        System.out.println("Controller Thread: " + Thread.currentThread().getName());

        List<Project> projects;

        if ("byId".equalsIgnoreCase(type) && value != null) {
            Project proj = projectService.getProjectById(Integer.parseInt(value));
            projects = proj != null ? List.of(proj) : new ArrayList<>();
        } else if ("byName".equalsIgnoreCase(type) && value != null) {
            projects = projectService.getProjectByName(value);
        } else if ("allActive".equalsIgnoreCase(type)) {
            projects = projectService.getAllActiveProjects();
        } else {
            projects = projectService.getAllProjects();
        }

        projectExcelExportService.generateExcelAsync(projects, response);
    }

}
