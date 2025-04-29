/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.entity;

/**
 *
 * @author raghav.mittal
 */
public class UserCredentials {
    
    private Integer userId;
    private String email;
    private String password;
    private Integer employeeId;
    private Boolean isAdmin;
    
    public UserCredentials(){};

    public UserCredentials(Integer userId, String email, String password, Integer employeeId,Boolean isAdmin) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.employeeId = employeeId;
        this.isAdmin = isAdmin;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    public Boolean getIsAdmin(){
        return isAdmin;
    }
    
    public void setIsAdmin(Boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "UserCredentials{" + "userId=" + userId + ", email=" + email + ", password=" + password + ", employeeId=" + employeeId +  ", isAdmin=" + isAdmin + '}';
    }
    
    
}
