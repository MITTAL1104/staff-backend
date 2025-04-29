/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.spectramd.focus.main.dao;

import java.util.Date;

/**
 *
 * @author raghav.mittal
 */
public interface TokenDAO {
    
    void saveToken(int employeeId,String token, Date expiry);
    
    boolean isTokenValid(String token);
    
    void deleteToken(String token);
    
//    void updateTokenExpiry(int employeeId,String token,Date expiryDate);
}
