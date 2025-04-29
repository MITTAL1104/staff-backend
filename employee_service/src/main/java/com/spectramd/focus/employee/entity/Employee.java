package com.spectramd.focus.employee.entity;

import java.util.Objects;



public class Employee {

    private Integer employeeId;
    private String name;
    private String email;
    private String dateOfJoining;
    private Integer roleId;
    private String roleName;
    private Boolean isActive;
    private Boolean isAdmin;

    public Employee() {
    }

    public Employee(Integer employeeId, String name, String email, String dateOfJoining, Integer roleId, String roleName, Boolean isActive, Boolean isAdmin) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.dateOfJoining = dateOfJoining;
        this.roleId = roleId;
        this.roleName = roleName;
        this.isActive = isActive;
        this.isAdmin = isAdmin;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "Employee{" + "employeeId=" + employeeId + ", name=" + name + ", email=" + email + ", dateOfJoining=" + dateOfJoining + ", roleId=" + roleId + ", roleName=" + roleName + ", isActive=" + isActive + ", isAdmin=" + isAdmin + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Employee other = (Employee) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.dateOfJoining, other.dateOfJoining)) {
            return false;
        }
        if (!Objects.equals(this.roleName, other.roleName)) {
            return false;
        }
        if (!Objects.equals(this.employeeId, other.employeeId)) {
            return false;
        }
        if (!Objects.equals(this.roleId, other.roleId)) {
            return false;
        }
        if (!Objects.equals(this.isActive, other.isActive)) {
            return false;
        }
        return Objects.equals(this.isAdmin, other.isAdmin);
    }
    
    
}
