/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spectramd.focus.main.service;

import com.spectramd.focus.main.entity.UserCredentials;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author raghav.mittal
 */
public interface AuthService {
    
    void login(String email,String password,HttpServletResponse response);
    
    void logout(String token);
    
    void register(UserCredentials user);
    
    public boolean emailExists(String email);
    
    public boolean  emailCredsExists(String email);
    
    public int getEmployeeIdByEmail(String email);
    
    public boolean getIsAdminByEmail(String email);
    
    void updatePassword(String email,String oldPassword,String newPassword);
    
    void registerWithDetails(Map<String,String> userData);
}
