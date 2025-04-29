package com.spectramd.focus.employee.dao;

import java.util.List;

import com.spectramd.focus.employee.entity.Employee;
import java.util.Map;

public interface EmployeeDAO {

    List<Employee> getAllEmployees();
    
    List<Employee> getAllActiveEmployees();

    Employee getEmployeeById(int id);
    
    List<Employee> getEmployeeByName(String name);

    Employee getEmployeeByNameForUpdate(String name);
    
    List<String> getEmployeeNames(String name);
    
    List<Integer> getEmployeeIds(String name);
    
    String getEmployeeNameByEmail(String email);
    
    List<Map<String,Object>> fetchAllRoles();
    
    List<Map<String,Object>>  getAllEmpNames();
    
    int getEmpIdByName(String name);

    int addEmployee(Employee employee);

    //int updateEmplpoyee(Employee employee);
    
    int updateEmployeeById(Employee employee);
    
    int updateEmployeeByName(Employee employee);
    
    int deleteAllEmployees();

    int deleteEmployeeById(int id);
    
    boolean empIdAllocationExists(int id);
    
    boolean empIdIsActiveCheck (int id);
 
    int deleteEmployeeByName(String name);
    
    boolean empNameAllocationExists(String name);
    
     boolean empNameIsActiveCheck (String name);
    
    boolean employeeIdExists(int id);

    boolean employeeNameExists(String name);
    
    boolean employeeExists();
    
    boolean checkActiveProjectOwner(int id);
    
    boolean checkDateOfJoiningConflict(int id,String newDateOfJoining);
}
