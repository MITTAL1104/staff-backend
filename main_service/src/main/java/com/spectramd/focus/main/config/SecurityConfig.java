package com.spectramd.focus.main.config;

import com.spectramd.focus.main.dao.TokenDAO;
import com.spectramd.focus.main.security.JwtFilter;
import com.spectramd.focus.main.security.JwtUtil;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private final TokenDAO tokenDAO;

    public SecurityConfig(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configure(http)) 
//                .authorizeHttpRequests(auth -> auth
//                        .antMatchers("/staff/login", "/staff/register","/staff/registerWithDetails").permitAll()
//                        .anyRequest().authenticated(  )
//                )
//
//                .addFilterBefore(new JwtFilter(jwtUtil,tokenDAO), BasicAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:3000")); // your frontend URL
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            config.setAllowCredentials(true); // important for cookies
            return config;
        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                .antMatchers("/staff/login", "/staff/register", "/staff/registerWithDetails").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Pre-flight requests
                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(jwtUtil, tokenDAO), BasicAuthenticationFilter.class)
                .logout(logout -> logout
                .logoutUrl("/staff/logout")
                .logoutSuccessHandler((request, response, authentication) -> {

                    String token = null;
                    if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                            if ("jwt".equals(cookie.getName())) {
                                token = cookie.getValue();
                                break;
                            }
                        }
                    }

                    if (token != null && !token.isEmpty()) {
                        tokenDAO.deleteToken(token);
                    }

                    ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(0)
                            .build();

                    ResponseCookie deleteCookie1 = ResponseCookie.from("refreshToken", "")
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(0)
                            .build();

                    response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie1.toString());
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Logout Successful");
                }));

        return http.build();
    }
}
