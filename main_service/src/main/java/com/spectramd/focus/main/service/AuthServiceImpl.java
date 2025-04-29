/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.service;

import com.spectramd.focus.main.dao.TokenDAO;
import com.spectramd.focus.main.dao.UserCredentialsDAO;
import com.spectramd.focus.main.entity.UserCredentials;
import com.spectramd.focus.main.security.JwtUtil;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author raghav.mittal
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private final UserCredentialsDAO userDAO;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final TokenDAO tokenDAO;

    public AuthServiceImpl(UserCredentialsDAO userDAO, TokenDAO tokenDAO, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenDAO = tokenDAO;
    }

    @Override
    public void login(String email, String password,HttpServletResponse response) {

        UserCredentials user = userDAO.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            String token = jwtUtil.generateToken(user.getEmail(), user.getEmployeeId(), user.getIsAdmin());

            Date expiryDate = jwtUtil.extractClaims(token).getExpiration();
            tokenDAO.saveToken(user.getEmployeeId(), token, expiryDate);
            
            Cookie cookie = new Cookie("jwt",token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60*60);
            response.addCookie(cookie);
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Override
    public void logout(String token) {
        tokenDAO.deleteToken(token);
    }

    @Override
    public void register(UserCredentials user) {
        if (!userDAO.emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email not found. Registration denied!");
        }
        
        if(userDAO.emailCredsExists(user.getEmail())){
            throw new IllegalArgumentException("User already exists!");
        }

        int employeeId = userDAO.getEmployeeIdByEmail(user.getEmail());
        boolean isAdmin = userDAO.getIsAdminByEmail(user.getEmail());

        user.setEmployeeId(employeeId);
        user.setIsAdmin(isAdmin);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userDAO.save(user);
    }
    

    @Override
    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }

    @Override
    public boolean emailCredsExists(String email) {
        return userDAO.emailCredsExists(email);
    }

    @Override
    public void registerWithDetails(Map<String, String> userData) {
        String email = userData.get("email");
        String password = passwordEncoder.encode(userData.get("password"));
        String name = userData.get("name");
        String role = userData.get("role");
        String dateOfJoining = userData.get("dateOfJoining");

        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        userDAO.insertIntoEmployeeTable(name, email, dateOfJoining, role);

        int employeeId = userDAO.getEmployeeIdByEmail(email);

        UserCredentials creds = new UserCredentials();
        creds.setEmail(email);
        creds.setPassword(password);
        creds.setEmployeeId(employeeId);
        creds.setIsAdmin(Boolean.FALSE);

        userDAO.save(creds);
    }

    @Override
    public int getEmployeeIdByEmail(String email) {
        return userDAO.getEmployeeIdByEmail(email);
    }

    @Override
    public boolean getIsAdminByEmail(String email) {
        return userDAO.getIsAdminByEmail(email);
    }

    @Override
    public void updatePassword(String email, String oldPassword, String newPassword) {
        UserCredentials user = userDAO.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found!");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect!");
        }

        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        userDAO.updatePassword(email, encryptedNewPassword);
    }
}
