package com.spectramd.focus.project.service;

import com.spectramd.focus.project.dao.ProjectDAO;
import com.spectramd.focus.project.entity.Project;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDAO projectDAO;

    public ProjectServiceImpl(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectDAO.getAllProjects();
    }
    
    @Override
    public List<Project> getAllActiveProjects(){
        return projectDAO.getAllActiveProjects();
    }

    @Override
    public Project getProjectById(int id) {
        return projectDAO.getProjectById(id);
    }

    @Override
    public List<Project> getProjectByName(String name) {
        return projectDAO.getProjectByName(name);
    }
    
    @Override
    public Project getProjectByNameForUpdate(String name){
        return projectDAO.getProjectByNameForUpdate(name);
    }
    
    @Override
    public List<String> getProjectNames(String name){
        return projectDAO.getProjectNames(name);
    }
    
    @Override
    public List<Integer> getProjectIds(String name){
        return projectDAO.getProjectIds(name);
    }

    @Override
    public int addProject(Project project) {
        return projectDAO.addProject(project);
    }

    @Override
    public int updateProjectById(Project project) {
        return projectDAO.updateProjectById(project);
    }

    @Override
    public int updateProjectByName(Project project) {
        return projectDAO.updateProjectByName(project);
    }
    
    @Override
    public int deleteAllProjects(){
        return projectDAO.deleteAllProjects();
    }

    @Override
    public int deleteProjectById(int id) {
        return projectDAO.deleteProjectById(id);
    }
    
    @Override
    public boolean projIdAllocationExists(int id){
        return projectDAO.projIdAllocationExists(id);
    }
    
    @Override
    public boolean projIdIsActiveCheck(int id){
        return projectDAO.projIdIsActiveCheck(id);
    }

    @Override
    public int deleteProjectByName(String name) {
        return projectDAO.deleteProjectByName(name);
    }
    
    @Override
    public boolean projNameAllocationExists(String name){
        return projectDAO.projNameAllocationExists(name);
    }
    
    @Override
    public boolean projNameIsActiveCheck(String name){
        return projectDAO.projNameIsActiveCheck(name);
    }

    @Override
    public boolean projectIdExists(int id) {
        return projectDAO.projectIdExists(id);
    }

    @Override
    public boolean projectNameExists(String name) {
        return projectDAO.projectNameExists(name);
    }
    
    @Override
    public boolean projectExists(){
        return projectDAO.projectExists();
    }
    
    @Override
    public boolean checkProjStartDateConflict(int id,String newStartDate){
        return projectDAO.checkProjStartDateConflict(id, newStartDate);
    }
    
    @Override
    public boolean checkProjEndDateConflict(int id,String newEndDate){
        return projectDAO.checkProjEndDateConflict(id, newEndDate);
    }
}
