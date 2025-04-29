/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.allocation.entity;

/**
 
 * @author raghav.mittal
 */
public class Allocation {

    private Integer allocationId;
    private Integer assigneeId;
    private String assigneeName;
    private Integer projectId;
    private String projectName;
    private String allocationStartDate;
    private String allocationEndDate;
    private String allocatorName;
    private Integer percentageAllocation = 100;
    private Boolean isActive;

    public Allocation() {
    }

    public Allocation(Integer allocationId, Integer assigneeId,String assigneeName, Integer projectId,String projectName, String allocationStartDate, String allocationEndDate, String allocatorName, Boolean isActive) {
        this.allocationId = allocationId;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.allocationStartDate = allocationStartDate;
        this.allocationEndDate = allocationEndDate;
        this.allocatorName = allocatorName;
        this.isActive = isActive;
    }

    public Integer getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Integer allocationId) {
        this.allocationId = allocationId;
    }

    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }
    
    public String getAssigneeName(){
        return assigneeName;
    }
    
    public void setAssigneeName(String assigneeName){
        this.assigneeName = assigneeName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName(){
        return projectName;
    }
    
    public void setProjectName(String projectName){
        this.projectName = projectName;
    }

    public String getAllocationStartDate() {
        return allocationStartDate;
    }

    public void setAllocationStartDate(String allocationStartDate) {
        this.allocationStartDate = allocationStartDate;
    }

    public String getAllocationEndDate() {
        return allocationEndDate;
    }

    public void setAllocationEndDate(String allocationEndDate) {
        this.allocationEndDate = allocationEndDate;
    }

    public String getAllocatorName() {
        return allocatorName;
    }

    public void setAllocatorName(String allocatorName) {
        this.allocatorName = allocatorName;
    }

    public Integer getPercentageAllocation() {
        return percentageAllocation;
    }

    public void setPercentageAllocation(Integer percentageAllocation) {
        this.percentageAllocation = percentageAllocation;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Allocation{" + "allocationId=" + allocationId + ", assigneeId=" + assigneeId + ", projectId=" + projectId + ", allocationStartDate=" + allocationStartDate + ", allocationEndDate=" + allocationEndDate + ", allocatorName=" + allocatorName + ", percentageAllocation=" + percentageAllocation + ", isActive=" + isActive + '}';
    }

}
