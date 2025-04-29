/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.allocation.service;

import com.spectramd.focus.allocation.dao.AllocationDAO;
import com.spectramd.focus.allocation.entity.Allocation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author raghav.mittal
 */
@Service
public class AllocationServiceImpl implements AllocationService {

    private final AllocationDAO allocationDAO;

    public AllocationServiceImpl(AllocationDAO allocationDAO) {
        this.allocationDAO = allocationDAO;
    }

    @Override
    public List<Allocation> getAllAllocations() {
        return allocationDAO.getAllAllocations();
    }

    @Override
    public List<Allocation> getAllActiveAllocations() {
        return allocationDAO.getAllActiveAllocations();
    }

    @Override
    public Allocation getByAllocationId(int id) {
        return allocationDAO.getByAllocationId(id);
    }

    @Override
    public List<Allocation> getAllAllocationByEmployeeName(String name) {
        return allocationDAO.getAllAllocationByEmployeeName(name);
    }

    @Override
    public Allocation getAllocationByEmployeeName(String name) {
        return allocationDAO.getAllocationByEmployeeName(name);
    }

    @Override
    public Allocation getSingleAllocationByProjectName(String name) {
        return allocationDAO.getSingleAllocationByProjectName(name);
    }

    @Override
    public List<Allocation> getAllocationByProjectName(String name) {
        return allocationDAO.getAllocationByProjectName(name);
    }

    @Override
    public List<Allocation> getByEmployeeId(int id) {
        return allocationDAO.getByEmployeeId(id);
    }

    @Override
    public List<Allocation> getByProjectId(int id) {
        return allocationDAO.getByProjectId(id);
    }

    @Override
    public List<Allocation> getAssigneeIdActiveAllocation(int id) {
        return allocationDAO.getAssigneeIdActiveAllocation(id);
    }

    @Override
    public List<String> getAllocatedEmployeeNames(String name) {
        return allocationDAO.getAllocatedEmployeeNames(name);
    }

    @Override
    public List<String> getAllocatedProjectNames(String name) {
        return allocationDAO.getAllocatedProjectNames(name);
    }

    @Override
    public List<Integer> getAllocatedIds(String name) {
        return allocationDAO.getAllocatedIds(name);
    }

    @Override
    public List<Integer> getAllocatedEmployeeIds(String name) {
        return allocationDAO.getAllocatedEmployeeIds(name);
    }

    @Override
    public List<Allocation> getAllocForDeleteByEmpName(String name) {
        return allocationDAO.getAllocForDeleteByEmpName(name);
    }

    @Override
    public List<Allocation> getAllocForDeleteByProjName(String name) {
        return allocationDAO.getAllocForDeleteByProjName(name);
    }

    @Override
    public int getEmpIdByName(String name) {
        return allocationDAO.getEmpIdByName(name);
    }

    @Override
    public int getProjIdByName(String name) {
        return allocationDAO.getProjIdByName(name);
    }

    @Override
    public int getProjIdByEmpId(int id) {
        return allocationDAO.getProjIdByEmpId(id);
    }

    @Override
    public int addAllocation(Allocation allocation) {
        return allocationDAO.addAllocation(allocation);
    }

    @Override
    public int updateAllocation(Allocation allocation) {
        return allocationDAO.updateAllocation(allocation);
    }

    @Override
    public int updateAllocationByProjId(Allocation allocation) {
        return allocationDAO.updateAllocationByProjId(allocation);
    }

    @Override
    public int deleteAllAllocations() {
        return allocationDAO.deleteAllAllocations();
    }

    @Override
    public int deleteByAllocationId(int id) {
        return allocationDAO.deleteByAllocationId(id);
    }

    @Override
    public int deleteByEmployeeId(int id) {
        return allocationDAO.deleteByEmployeeId(id);
    }

    @Override
    public int deleteByProjectId(int id) {
        return allocationDAO.deleteByProjectId(id);
    }
   
    @Override
    public int deleteAllEmpName(String name){
        return allocationDAO.deleteAllEmpName(name);
    }
    
    @Override
    public int deleteAllProjName(String name){
        return allocationDAO.deleteAllProjName(name);
    }

    @Override
    public boolean allocationIdExists(int id) {
        return allocationDAO.allocationIdExists(id);
    }

    @Override
    public boolean allocationAssigneeIdExists(int id) {
        return allocationDAO.allocationAssigneeIdExists(id);
    }

    @Override
    public boolean allocationProjectIdExists(int id) {
        return allocationDAO.allocationProjectIdExists(id);
    }

    @Override
    public boolean empJoiningDateCheck(String allocationStartDate, int assigneeId) {
        return allocationDAO.empJoiningDateCheck(allocationStartDate, assigneeId);
    }

    @Override
    public boolean projectDatesCheck(String allocationStartDate, String allocationEndDate, int projectId) {
        return allocationDAO.projectDatesCheck(allocationStartDate, allocationEndDate, projectId);
    }

    @Override
    public boolean datesOverlapCheckForActiveAllocation(int assigneeId, String newStartDateStr, String newEndDateStr) {

        LocalDate newStartDate = LocalDate.parse(newEndDateStr);
        LocalDate newEndDate = LocalDate.parse(newEndDateStr);

        List<Allocation> activeAllocations = allocationDAO.getAssigneeIdActiveAllocation(assigneeId);

        for (Allocation existing : activeAllocations) {
            LocalDate existingStart = LocalDate.parse(existing.getAllocationStartDate());
            LocalDate existingEnd = LocalDate.parse(existing.getAllocationStartDate());

            if ((newStartDate.isBefore(existingEnd) && newEndDate.isAfter(existingStart))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAllocationExistsForSameProject(int assigneeId, int projectId) {
        return allocationDAO.isAllocationExistsForSameProject(assigneeId, projectId);
    }
    
    @Override
    public boolean employeeIsActiveCheck(int assigneeId){
        return allocationDAO.employeeIsActiveCheck(assigneeId);
    }
    
    @Override
    public boolean projectIsActiveCheck(int projectId){
        return allocationDAO.projectIsActiveCheck(projectId);
    }
}
