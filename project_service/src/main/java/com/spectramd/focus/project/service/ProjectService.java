package com.spectramd.focus.project.service;

import com.spectramd.focus.project.entity.Project;
import java.util.List;

public interface ProjectService {

    List<Project> getAllProjects();
    
    List<Project> getAllActiveProjects();

    Project getProjectById(int id);

    List<Project> getProjectByName(String name);
    
    Project getProjectByNameForUpdate(String name);
    
    List<String> getProjectNames(String name);
    
    List<Integer> getProjectIds(String name);

    int addProject(Project project);

    //int updateProject(Project project);
    
    int updateProjectById(Project project);
    
    int updateProjectByName(Project project);
    
    int deleteAllProjects();

    int deleteProjectById(int id);
    
    boolean projIdAllocationExists(int id);
    
    boolean projIdIsActiveCheck (int id);

    int deleteProjectByName(String name);
    
    boolean projNameAllocationExists(String name);
    
    boolean projNameIsActiveCheck (String name);

    boolean projectIdExists(int id);

    boolean projectNameExists(String name);
    
    boolean projectExists();
    
    boolean checkProjStartDateConflict(int id,String newStartDate);
    
    boolean checkProjEndDateConflict(int id,String newEndDate);
}
