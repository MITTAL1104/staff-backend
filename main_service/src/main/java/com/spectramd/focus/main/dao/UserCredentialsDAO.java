/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spectramd.focus.main.dao;

import com.spectramd.focus.main.entity.UserCredentials;

/**
 *
 * @author raghav.mittal
 */
public interface UserCredentialsDAO {

    UserCredentials findByEmail(String email);

    void save(UserCredentials user);

    public boolean emailExists(String email);
    
    public boolean emailCredsExists(String email);
    
    public int getEmployeeIdByEmail(String email);
    
    public boolean getIsAdminByEmail(String email);
    
    void updatePassword(String email,String newPassword);
    
    void insertIntoEmployeeTable(String name,String email,String dateOfJoining,String role);
}
