package com.spectramd.focus.employee.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.spectramd.focus.employee.entity.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    @Autowired
    private final DataSource dataSource;


    private static final String GET_ALL_EMPLOYEES_QUERY = "select e.employeeId,e.name,e.email,e.dateOfJoining,e.roleId,r.roleName,e.isActive,e.isAdmin from employee_staff e join roles_staff r on e.roleId=r.roleId";

    private static final String GET_ALL_ACTIVE_EMPLOYEES_QUERY = "select e.employeeId,e.name,e.email,e.dateOfJoining,e.roleId,r.roleName,e.isActive,e.isAdmin from employee_staff e join roles_staff r on e.roleId=r.roleId where e.isActive=1";

    private static final String GET_EMPLOYEE_BY_ID = "select e.employeeId,e.name,e.email,e.dateOfJoining,e.roleId,r.roleName,e.isActive,e.isAdmin from employee_staff e join roles_staff r on e.roleId=r.roleId where e.employeeId=?";

    private static final String GET_EMPLOYEE_BY_NAME = "select e.employeeId,e.name,e.email,e.dateOfJoining,e.roleId,r.roleName,e.isActive,e.isAdmin from employee_staff e join roles_staff r on e.roleId=r.roleId where e.name like ?";
    
    private static final String GET_EMPLOYEE_NAMES = "select name from employee_staff where cast(name AS NCHAR) like ?";
    
    private static final String GET_EMPLOYEE_IDS = "select employeeId from employee_staff where name like ?";
    
    private static final String GET_EMP_NAME_BY_EMAIL = "select name from employee_staff where email=?";
    
    private static final String GET_ALL_ROLES = "select roleId,roleName from roles_staff";

    private static final String ADD_EMPLOYEE = "insert into employee_staff(name,email,dateOfJoining,roleId,isActive,isAdmin) values(?,?,?,(select roleId FROM roles_staff WHERE roleName = ?),?,?)";

    private static final String UPDATE_EMPLOYEE_BY_ID = "update employee_staff set  name=?,dateOfJoining=?,roleId=?,isActive=?,isAdmin=? where employeeId=?";

    private static final String UPDATE_EMPLOYEE_BY_NAME = "update employee_staff set name=?,dateOfJoining=?,roleId=?,isActive=?,isAdmin=? where name like ?";

    private static final String DELETE_ALL_EMPLOYEES = "delete from employee_staff";

    private static final String DELETE_EMPLOYEE_BY_ID = "update employee_staff set isActive = 0 where employeeId=?";
    
    private static final String EMP_ID_ALLOCATION_EXISTS = "select count(*) from allocation_staff where assigneeId=?";
    
    private static final String EMP_ID_IS_ACTIVE_CHECK = "select isActive from employee_staff where employeeId=?";

    private static final String DELETE_EMPLOYEE_BY_NAME = "update employee_staff set isActive = 0 where name like ?";
    
    private static final String EMP_NAME_ALLOCATION_EXISTS = "select count(*) from allocation_staff a join employee_staff e on a.assigneeId = e.employeeId where e.name=?";
    
    private static final String EMP_NAME_IS_ACTIVE_CHECK = "select isActive from employee_staff where name=?";

    private static final String EMPLOYEE_ID_EXISTS = "select count(*) from employee_staff where employeeId=?";

    private static final String EMPLOYEE_NAME_EXISTS = "select count(*) from employee_staff where name like ?";
    
    private static final String EMPLOYEE_EXISTS = "select count(*) from employee_staff";
    
    private static final String GET_ALL_EMP_NAMES = "select employeeId,name from employee_staff where roleId in (4,5,9,10,14,15) and isActive=1";

    private static final String CHECK_PROJECT_OWNER = "select count(*) from project_staff where projectOwnerId=? and isActive=1";
    
    private static final String CHECK_DOJ_CONFLICT = "select count(*) from allocation_staff a join employee_staff e on a.assigneeId = e.employeeId where e.employeeId=? and a.isActive=1 and a.allocationStartDate<?";
    
    private static final String GET_EMP_ID_BY_NAME = "select employeeId from employee_staff where name=?";
            
    public EmployeeDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); 
                PreparedStatement ps = connection.prepareStatement(GET_ALL_EMPLOYEES_QUERY); 
                ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
                    }

            } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employees", e);
                 }
        return employees;
    }

    /*
    @Override
    public List<Employee> getAllEmployees() {
        return jdbcTemplate.query(GET_ALL_EMPLOYEES_QUERY, new EmployeeRowMapper());
    }
     */
    
    
    
    @Override
    public Employee getEmployeeById(int id) {
        Employee employee = null;

        try (
                Connection connection = dataSource.getConnection(); 
                PreparedStatement ps = connection.prepareStatement(GET_EMPLOYEE_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = mapRowToEmployee(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee by ID", e);
        }

        return employee;
    }

    /*
    @Override
    public Employee getEmployeeById(int id) {
        List<Employee> employees = jdbcTemplate.query(GET_EMPLOYEE_BY_ID, new EmployeeRowMapper(), id);
        return employees.isEmpty() ? null : employees.get(0);
    }
     */
    
        @Override
    public List<Employee> getAllActiveEmployees() {
        List<Employee> employees = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); 
                PreparedStatement ps = connection.prepareStatement(GET_ALL_ACTIVE_EMPLOYEES_QUERY); 
                ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
                    }

            } catch (SQLException e) {
            throw new RuntimeException("Error retrieving active employees", e);
                 }
        return employees;
    }
    
    @Override
    public List<Employee> getEmployeeByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); 
                PreparedStatement ps = connection.prepareStatement(GET_EMPLOYEE_BY_NAME)) {
            
                ps.setString(1,pattern);
                try(ResultSet rs = ps.executeQuery()){
                    
                    while (rs.next()) {
                employees.add(mapRowToEmployee(rs));
                    }

            } 
        }catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee by name", e);
                 }
        return employees;
    }
    
    
    @Override
    public Employee getEmployeeByNameForUpdate(String name) {
        Employee employee = null;
        String pattern = "%" + name + "%";
        try (
                Connection connection = dataSource.getConnection(); 
                PreparedStatement ps = connection.prepareStatement(GET_EMPLOYEE_BY_NAME)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = mapRowToEmployee(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee by name for updation", e);
        }

        return employee;
    }
    
    /*
    @Override
    public Employee getEmployeeByName(String name) {
        String pattern = "%" + name + "%";
        List<Employee> employees = jdbcTemplate.query(GET_EMPLOYEE_BY_NAME, new EmployeeRowMapper(), pattern);
        return employees.isEmpty() ? null : employees.get(0);
    }
    */
    
    @Override 
    public List<String> getEmployeeNames(String name){
        
        List<String> names = new ArrayList<>();
        String pattern = "%" + name + "%";
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_EMPLOYEE_NAMES)
                ){
            ps.setString(1,pattern);
            
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    names.add(rs.getString("name"));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error retrieving employee names",e);
        }
        
        return names;
    }
    
    @Override
    public List<Integer> getEmployeeIds(String name){
        List<Integer> Ids = new ArrayList<>();
        String pattern = "%" + name + "%";
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_EMPLOYEE_IDS)){
            
            ps.setString(1,pattern);
            
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Ids.add(rs.getInt("employeeId"));
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error retrieving employee IDs");
        }
        
        return Ids;
    }
    
    
    @Override 
    public String getEmployeeNameByEmail(String email){
        
        String name =null;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_EMP_NAME_BY_EMAIL)
                ){
            
            ps.setString(1,email);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                        name = rs.getString("name");
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error retrieving employee name by email",e);
        }
        
        return name;
    }
    
    @Override
    public List<Map<String,Object>>fetchAllRoles(){
        
        List<Map<String,Object>> roles = new ArrayList<>();
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_ALL_ROLES);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                Map<String,Object> role = new HashMap<>();
                role.put("roleId", rs.getInt("roleId"));
                role.put("roleName", rs.getString("roleName"));
                roles.add(role);
            }
        }catch(SQLException e){
            throw new RuntimeException("Error fetching roles",e);
        }
        
        return roles;
    }
    
    @Override
    public List<Map<String,Object>> getAllEmpNames(){
        List<Map<String,Object>> names = new ArrayList<>();
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_ALL_EMP_NAMES);
                ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                Map<String,Object> name = new HashMap<>();
                name.put("employeeId",rs.getInt("employeeId"));
                name.put("name",rs.getString("name"));
                names.add(name);
            }
            
            
        } catch(SQLException e){
            throw new RuntimeException("Error retrieving all employee names",e);
        }
        
        return names;
    }
    
    @Override
    public int getEmpIdByName(String name){
        int id=-1;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(GET_EMP_ID_BY_NAME)){
            
            ps.setString(1, name);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    id=rs.getInt(1);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error fetching employee ID by name",e);
        }
        
        return id;
    }
    
    
    @Override
    public int addEmployee(Employee employee) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(ADD_EMPLOYEE)) {
            
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getDateOfJoining());
            ps.setString(4, employee.getRoleName());
            ps.setBoolean(5, employee.getIsActive());
            ps.setBoolean(6, employee.getIsAdmin());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding new employee", e);
        }
    }
    

    /*
    @Override
    public int addEmployee(Employee employee) {
        return jdbcTemplate.update(ADD_EMPLOYEE, employee.getName(), employee.getEmail(), employee.getDateOfJoining(),
                employee.getRoleId(), employee.getIsActive(), employee.getIsAdmin());
    }
    */

    @Override
    public int updateEmployeeById(Employee employee){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_EMPLOYEE_BY_ID)){
            
            ps.setString(1,employee.getName());
            ps.setString(2,employee.getDateOfJoining());
            ps.setInt(3,employee.getRoleId());
            ps.setBoolean(4, employee.getIsActive());
            ps.setBoolean(5, employee.getIsAdmin());
            ps.setInt(6,employee.getEmployeeId());
            
            return ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error updating employee by ID",e);
        }
    }
    
    /*
    @Override
    public int updateEmployeeById(Employee employee) {
        return jdbcTemplate.update(UPDATE_EMPLOYEE_BY_ID, employee.getRoleId(), employee.getIsActive(), employee.getIsAdmin(), employee.getEmployeeId());
    }
    */
    
    @Override
    public int updateEmployeeByName(Employee employee){
        String pattern = "%" + employee.getName() + "%";
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_EMPLOYEE_BY_NAME)){
            ps.setString(1,employee.getName());
            ps.setString(2,employee.getDateOfJoining());
            ps.setInt(3,employee.getRoleId());
            ps.setBoolean(4, employee.getIsActive());
            ps.setBoolean(5, employee.getIsAdmin());
            ps.setString(6, pattern);
            
            return ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error updating employee by name",e);
        }
    }

    /*
    @Override
    public int updateEmployeeByName(Employee employee) {
        String pattern = "%" + employee.getName() + "%";
        return jdbcTemplate.update(UPDATE_EMPLOYEE_BY_NAME, employee.getRoleId(), employee.getIsActive(), employee.getIsAdmin(), pattern);
    }
    */
    
    @Override
    public int deleteAllEmployees() {
        int rowsAffected = 0;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_ALL_EMPLOYEES)) {

        rowsAffected = ps.executeUpdate();  // Execute the delete query
        
        
        } catch (SQLException e) {

        throw new RuntimeException("Error deleting all employees", e);
        }

    return rowsAffected;  // Return the number of rows deleted
}

    /*
    @Override
    public int deleteAllEmployees() {
        return jdbcTemplate.update(DELETE_ALL_EMPLOYEES);
    }
    */
    
    @Override
    public int deleteEmployeeById(int id){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_EMPLOYEE_BY_ID)){
            
            ps.setInt(1, id);
            
            return ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error deleting employee " + e);
        }
    }
    
    @Override
    public boolean empIdAllocationExists(int id){
        boolean exists = false;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMP_ID_ALLOCATION_EXISTS)){
            
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    exists=rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking if allocation for employee ID exists", e);
        }
        return exists;
    }
    
    @Override
    public boolean empIdIsActiveCheck(int id){
      
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMP_ID_IS_ACTIVE_CHECK)){
            
            ps.setInt(1, id);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getBoolean("isActive");
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking whether employee Id is active",e);
        }
        return false;
    }
    
    /*
    @Override
    public int deleteEmployeeById(int id) {
        return jdbcTemplate.update(DELETE_EMPLOYEE_BY_ID, id);
    }
    */
    
    @Override
    public int deleteEmployeeByName(String name){
        String pattern = "%" + name + "%";
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_EMPLOYEE_BY_NAME)){
            
            ps.setString(1, pattern);
            
            return ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error deleting employee by name",e);
        }
    }
    
    @Override
    public boolean empNameAllocationExists(String name){
        boolean exists = false;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMP_NAME_ALLOCATION_EXISTS)){
            
            ps.setString(1, name);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    exists=rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking if allocation for employee Name exists", e);
        }
        return exists;
    }
    
    @Override
    public boolean empNameIsActiveCheck(String name){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMP_NAME_IS_ACTIVE_CHECK)){
            
            ps.setString(1, name);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getBoolean("isActive");
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking whether employee name is active",e);
        }
        return false;
    }    
    /*
    @Override
    public int deleteEmployeeByName(String name) {
        String pattern = "%" + name + "%";
        return jdbcTemplate.update(DELETE_EMPLOYEE_BY_NAME, pattern);
    }
    */
    
    @Override
    public boolean employeeIdExists(int id){
        boolean exists = false;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMPLOYEE_ID_EXISTS)){
            
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    exists=rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking if employee ID exists", e);
        }
        return exists;
    }

    /*
    @Override
    public boolean employeeIdExists(int id) {
        Integer count = jdbcTemplate.queryForObject(EMPLOYEE_ID_EXISTS, Integer.class, id);
        return count != null && count > 0;
    }
    */

    @Override
    public boolean employeeNameExists(String name){
        boolean exists = false;
        String pattern = "%" + name + "%";
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMPLOYEE_NAME_EXISTS)){
            
            ps.setString(1, pattern);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    exists=rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking if employee name exists", e);
        }
        return exists;
    }
    
   /*
    @Override
    public boolean employeeNameExists(String name) {
        String pattern = "%" + name + "%";
        Integer count = jdbcTemplate.queryForObject(EMPLOYEE_NAME_EXISTS, Integer.class, pattern);
        return count != null && count > 0;
    }
    */
    
    @Override
    public boolean employeeExists(){
        boolean exists = false;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(EMPLOYEE_EXISTS)){
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    exists=rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking if employee exists", e);
        }
        return exists;
    }
    
    @Override
    public boolean checkActiveProjectOwner(int id){
        boolean isOwner=false;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(CHECK_PROJECT_OWNER)){
            
            ps.setInt(1,id);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    isOwner = rs.getInt(1)>0;
                }
            }
            
        }catch(SQLException e){
            throw new RuntimeException("Error checking if employee is a project owner",e);
        }
        
        return isOwner;
        
    }
    
    @Override
    public boolean checkDateOfJoiningConflict(int id,String newDateOfJoining){
        boolean conflict = false;
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(CHECK_DOJ_CONFLICT)){
            
            ps.setInt(1, id);
            ps.setString(2, newDateOfJoining);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    conflict = rs.getInt(1)>0;
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error checking Date Of Joining conflict",e);
        }
        
        return conflict;
    }

    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employeeId"));
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setRoleId(rs.getInt("roleId"));
        employee.setRoleName(rs.getString("roleName"));
        employee.setDateOfJoining(rs.getString("dateOfJoining")); 
        employee.setIsActive(rs.getBoolean("isActive"));
        employee.setIsAdmin(rs.getBoolean("isAdmin"));
        return employee;
    }
}
