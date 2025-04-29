/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

/**
 *
 * @author raghav.mittal
 */

@Repository
public class TokenDAOImpl implements TokenDAO{
    
    private final DataSource dataSource;
    
    public TokenDAOImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    
    private static final String SAVE_TOKEN = "insert into token_staff(employeeId,token,expiry) VALUES(?,?,?)";
    
    private static final String TOKEN_VALID = "select count(*) from token_staff where token=? and expiry>SYSDATETIME()";
    
    private static final String DELETE_TOKEN = "delete from token_staff where token=?";
    
//   private static final String UPDATE_EXPIRY = "update token_staff set expiry=? where employeeId=? and token=?";
    
   
    @Override
    public void saveToken(int employeeId,String token,Date expiry){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(SAVE_TOKEN)){
            
            ps.setInt(1,employeeId);
            ps.setString(2,token);
            ps.setTimestamp(3,new java.sql.Timestamp(expiry.getTime()));
            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error saving token",e);
        }
    }
    
    @Override
    public boolean isTokenValid(String token){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(TOKEN_VALID)){
            ps.setString(1, token);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1)>0;
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error checking token validity",e);
        }
        
        return false;
    }
    
    @Override
    public void deleteToken(String token){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_TOKEN)){
            
            ps.setString(1,token);
            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error deleting token",e);
        }
    }
    
//    @Override
//    public void updateTokenExpiry(int employeeId,String token,Date expiryDate){
//        try(
//                Connection connection = dataSource.getConnection();
//                PreparedStatement ps = connection.prepareStatement(UPDATE_EXPIRY)){
//            
//            ps.setInt(1, employeeId);
//            ps.setString(2, token);
//            ps.setTimestamp(3,new java.sql.Timestamp(expiryDate.getTime()));
//            
//            ps.executeUpdate();
//        }catch(SQLException e){
//            throw new RuntimeException("Error saving token",e);
//        }
//    }
    
}
