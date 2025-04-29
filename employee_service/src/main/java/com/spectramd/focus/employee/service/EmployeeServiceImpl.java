package com.spectramd.focus.employee.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spectramd.focus.employee.dao.EmployeeDAO;
import com.spectramd.focus.employee.entity.Employee;
import java.util.Map;


@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeServiceImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
    
    @Override
    public List<Employee> getAllActiveEmployees(){
        return employeeDAO.getAllActiveEmployees();
    }

    @Override
    public Employee getEmployeeById(int id) {
        Employee employee = employeeDAO.getEmployeeById(id);
        return employee;
    }
    
    @Override
    public List<Employee> getEmployeeByName(String name){
        return employeeDAO.getEmployeeByName(name);
    }
    
    
    @Override
    public Employee getEmployeeByNameForUpdate(String name) {
        return employeeDAO.getEmployeeByNameForUpdate(name);
    }

    @Override
    public List<String> getEmployeeNames(String name){
        return employeeDAO.getEmployeeNames(name);
    }
    
    @Override
    public List<Integer> getEmployeeIds(String name){
        return employeeDAO.getEmployeeIds(name);
    }
    
    @Override
    public String getEmployeeNameByEmail(String email){
        return employeeDAO.getEmployeeNameByEmail(email);
    }
    
    @Override 
    public List<Map<String,Object>> fetchAllRoles(){
        return employeeDAO.fetchAllRoles();
    }
    
    @Override
    public int getEmpIdByName(String name){
        return employeeDAO.getEmpIdByName(name);
    }
    
    @Override
    public List<Map<String,Object>>  getAllEmpNames(){
        return employeeDAO.getAllEmpNames();
    }
   
    @Override
    public int addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }

//    @Override
//    public int updateEmplpoyee(Employee employee) {
//        return employeeDAO.updateEmplpoyee(employee);
//    }
    
    @Override
    public int updateEmployeeById(Employee employee) {
        return employeeDAO.updateEmployeeById(employee);
    }
    
    @Override
    public int updateEmployeeByName(Employee employee) {
        return employeeDAO.updateEmployeeByName(employee);
    }

    @Override
    public int deleteAllEmployees() {
        return employeeDAO.deleteAllEmployees();
    }
    
    @Override
    public int deleteEmployeeById(int id) {
        return employeeDAO.deleteEmployeeById(id);
    }
    
    @Override
    public boolean empIdAllocationExists(int id){
        return employeeDAO.empIdAllocationExists(id);
    }
    
    @Override
    public boolean empIdIsActiveCheck(int id){
        return employeeDAO.empIdIsActiveCheck(id);
    }

    @Override
    public int deleteEmployeeByName(String name) {
        return employeeDAO.deleteEmployeeByName(name);
    }
    
    @Override
    public boolean empNameAllocationExists(String name){
        return employeeDAO.empNameAllocationExists(name);
    }
    
    @Override
    public boolean empNameIsActiveCheck(String name){
        return employeeDAO.empNameIsActiveCheck(name);
    }

    @Override
    public boolean employeeIdExists(int id) {
        return employeeDAO.employeeIdExists(id);
    }

    @Override
    public boolean employeeNameExists(String name) {
        return employeeDAO.employeeNameExists(name);
    }
    
    @Override
    public boolean employeeExists(){
        return employeeDAO.employeeExists();
    }
    
    @Override
    public boolean checkActiveProjectOwner(int id){
        return employeeDAO.checkActiveProjectOwner(id);
    }
    
    @Override
    public boolean checkDateOfJoiningConflict(int id,String newDateOfJoining){
        return employeeDAO.checkDateOfJoiningConflict(id, newDateOfJoining);
    }
}
