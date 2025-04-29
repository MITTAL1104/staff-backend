/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spectramd.focus.allocation.dao;

import com.spectramd.focus.allocation.entity.Allocation;
import java.util.List;

/**
 *
 * @author raghav.mittal
 */
public interface AllocationDAO {

    List<Allocation> getAllAllocations();

    List<Allocation> getAllActiveAllocations();

    Allocation getByAllocationId(int id);

    List<Allocation> getAllAllocationByEmployeeName(String name);

    Allocation getAllocationByEmployeeName(String name);

    Allocation getSingleAllocationByProjectName(String name);

    List<Allocation> getAllocationByProjectName(String name);

    List<Allocation> getByEmployeeId(int id);

    List<Allocation> getByProjectId(int id);

    List<Allocation> getAssigneeIdActiveAllocation(int id);

    List<String> getAllocatedEmployeeNames(String name);

    List<String> getAllocatedProjectNames(String name);

    List<Integer> getAllocatedIds(String name);

    List<Integer> getAllocatedEmployeeIds(String name);

    List<Allocation> getAllocForDeleteByEmpName(String name);

    List<Allocation> getAllocForDeleteByProjName(String name);

    int getEmpIdByName(String name);

    int getProjIdByName(String name);

    int getProjIdByEmpId(int id);

    int addAllocation(Allocation allocation);

    int updateAllocation(Allocation allocation);

    int updateAllocationByProjId(Allocation allocation);

    int deleteAllAllocations();

    int deleteByAllocationId(int id);

    int deleteByEmployeeId(int id);

    int deleteByProjectId(int id);

    int deleteAllEmpName(String name);

    int deleteAllProjName(String name);

    boolean allocationIdExists(int id);

    boolean allocationAssigneeIdExists(int id);

    boolean allocationProjectIdExists(int id);

    public boolean empJoiningDateCheck(String allocationStartDate, int assigneeId);

    public boolean projectDatesCheck(String allocationStartDate, String allocationEndDate, int projectId);

    public boolean isAllocationExistsForSameProject(int assigneeId, int projectId);

    public boolean employeeIsActiveCheck(int assigneeId);

    public boolean projectIsActiveCheck(int projectId);

}
