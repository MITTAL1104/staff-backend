/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.dao;

import com.spectramd.focus.main.entity.UserCredentials;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author raghav.mittal
 */

@Repository
public class UserCredentialsDAOImpl implements UserCredentialsDAO {

    @Autowired
    private final DataSource dataSource;

    public UserCredentialsDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final String FIND_BY_EMAIL = "select userId, email, password, employeeId, isAdmin from credentials_staff where email=?";

    private final String ADD_USER = "insert into credentials_staff(email,password,employeeId,isAdmin) values(?,?,?,?)";

    private final String EMAIL_EXISTS = "select count(*) from employee_staff where email=?";
    
    private final String EMAIL_CREDS_EXISTS = "select count(*) from credentials_staff where email=?";
    
    private final String FETCH_EMP_ID = "select employeeId from employee_staff where email=?";
    
    private final String FETCH_IS_ADMIN = "select isAdmin from employee_staff where email=?";
    
    private final String UPDATE_PASSWORD = "update credentials_staff set password=? where email=?";
    
    private final String INSERT_EMPLOYEE = "insert into employee_staff(name,email,dateOfJoining,roleId,isActive,isAdmin) values(?,?,?,(select roleId FROM roles_staff WHERE roleName = ?),1,0)";

    @Override
    public UserCredentials findByEmail(String email) {

        UserCredentials user = null;

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUserCredentials(rs);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error retrieving user by email", e);
        }
        return user;
    }
    
    @Override 
    public void save(UserCredentials user){
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(ADD_USER)){
            
            ps.setString(1,user.getEmail());
            ps.setString(2,user.getPassword());
            ps.setInt(3,user.getEmployeeId());
            ps.setBoolean(4,user.getIsAdmin());
            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error saving user credentials", e);
        }
    }
    
    @Override
    public boolean emailExists(String email){
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(EMAIL_EXISTS)){
            
            ps.setString(1, email);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking existence of email in employee table", e);
        }
        return false;
    }
    
    @Override
    public boolean emailCredsExists(String email){
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(EMAIL_CREDS_EXISTS)){
            
            ps.setString(1, email);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking existence of email in credentials table", e);
        }
        return false;
    }
    
    @Override
    public int getEmployeeIdByEmail(String email){
        int employeeId=-1;
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(FETCH_EMP_ID)){
            
            ps.setString(1,email);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    employeeId=rs.getInt("employeeId");
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error fetching employee ID by email", e);
        }
        
        // not adding check for empId not found as it will be implmented after the email id check
        return employeeId;
    }
    
    @Override
    public boolean getIsAdminByEmail(String email){
        boolean isAdmin = false;
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(FETCH_IS_ADMIN)){
            
            ps.setString(1,email);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    isAdmin = rs.getBoolean("isAdmin");
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error fetching isAdmin by email",e);
        }
        
        return isAdmin;
    }
    
    @Override
    public void updatePassword(String email,String password){
        try(Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_PASSWORD)){
            
            ps.setString(1, password);
            ps.setString(2,email);
            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error updating password",e);
        }
    }
    
    @Override 
    public void insertIntoEmployeeTable(String name,String email,String dateOfJoining,String role){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_EMPLOYEE)){
            
            ps.setString(1,name);
            ps.setString(2, email);
            ps.setString(3, dateOfJoining);
            ps.setString(4, role);
            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Failed to insert employee", e);
        }
    }
    

    private UserCredentials mapRowToUserCredentials(ResultSet rs) throws SQLException{
        
        UserCredentials user = new UserCredentials();
        
        user.setUserId(rs.getInt("userId"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setEmployeeId(rs.getInt("employeeId"));
        user.setIsAdmin(rs.getBoolean("isAdmin"));
        
        return user;
    }

}