package com.spectramd.focus.project.entity;

public class Project {

    private Integer projectId;
    private String projectName;
    private String description;
    private Integer projectOwnerId;
    private String projectOwnerName;
    private String startDate;
    private String endDate;
    private Boolean isActive;

    public Project() {
    }

    ;

    public Project(Integer projectId, String projectName, String description, Integer projectOwnerId, String projectOwnerName, String startDate, String endDate, Boolean isActive) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.projectOwnerId = projectOwnerId;
        this.projectOwnerName = projectOwnerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProjectOwnerId() {
        return projectOwnerId;
    }

    public void setProjectOwnerId(Integer projectOwnerId) {
        this.projectOwnerId = projectOwnerId;
    }
    
    public String getProjectOwnerName() {
        return projectOwnerName;
    }

    public void setProjectOwnerName(String projectOwnerName) {
        this.projectOwnerName = projectOwnerName;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Project{" + "projectId=" + projectId + ", projectName=" + projectName + ", description=" + description + ", projectOwnerId=" + projectOwnerId + ", projectOwnerName=" + projectOwnerName + ", startDate=" + startDate + ", endDate=" + endDate + ", isActive=" + isActive + '}';
    }

}
