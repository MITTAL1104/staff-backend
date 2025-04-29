/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.entity;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author raghav.mittal
 */
public class Tokens {
    
    private Integer tokenId;
    private Integer employeeId;
    private String token;
    private Date issuedAt;
    private Date expiry;
    
    public Tokens(){};

    public Tokens(Integer tokenId, Integer employeeId, String token, Date issuedAt, Date expiry) {
        this.tokenId = tokenId;
        this.employeeId = employeeId;
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiry = expiry;
    }

    public Integer getTokenId() {
        return tokenId;
    }

    public void setTokenId(Integer tokenId) {
        this.tokenId = tokenId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.tokenId);
        hash = 79 * hash + Objects.hashCode(this.employeeId);
        hash = 79 * hash + Objects.hashCode(this.token);
        hash = 79 * hash + Objects.hashCode(this.issuedAt);
        hash = 79 * hash + Objects.hashCode(this.expiry);
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
        final Tokens other = (Tokens) obj;
        if (!Objects.equals(this.token, other.token)) {
            return false;
        }
        if (!Objects.equals(this.tokenId, other.tokenId)) {
            return false;
        }
        if (!Objects.equals(this.employeeId, other.employeeId)) {
            return false;
        }
        if (!Objects.equals(this.issuedAt, other.issuedAt)) {
            return false;
        }
        return Objects.equals(this.expiry, other.expiry);
    }

    @Override
    public String toString() {
        return "Tokens{" + "tokenId=" + tokenId + ", employeeId=" + employeeId + ", token=" + token + ", issuedAt=" + issuedAt + ", expiry=" + expiry + '}';
    }
    
    
}
