/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.main.controller;

import com.spectramd.focus.main.entity.UserCredentials;
import com.spectramd.focus.main.security.JwtUtil;
import com.spectramd.focus.main.service.AuthService;
import io.jsonwebtoken.Claims;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 *
 * @author raghav.mittal
 */
@RestController
@RequestMapping("/staff")
@CrossOrigin(origins = "http://localhost:3000")
public class StaffController {

    private final WebClient webClient;

    private final AuthService authService;

    private final JwtUtil jwtUtil;

    public StaffController(WebClient.Builder webClientBuilder, AuthService authService, JwtUtil jwtUtil) {
        this.webClient = webClientBuilder.build();
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getAuthDetails(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = null;
        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Claims claims = jwtUtil.extractClaims(token);

        Map<String, Object> response = new HashMap<>();
        response.put("email", claims.getSubject());
        response.put("employeeId", claims.get("employeeId"));
        response.put("isAdmin", claims.get("isAdmin"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCredentials loginRequest, HttpServletResponse response) {

        try {

            if (!authService.emailCredsExists(loginRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User does not exists.Please signup");
            }

            authService.login(loginRequest.getEmail(), loginRequest.getPassword(), response);
            return ResponseEntity.ok("Login Successful");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
        }
    }

//
//            ResponseCookie cookie = ResponseCookie.from("token",token)
//                    .httpOnly(true)
//                    .secure(false)
//                    .path("/")
//                    .maxAge(Duration.ofHours(1))
//                    .sameSite("Lax")
//                    .build();
//            
//            response.setHeader(HttpHeaders.SET_COOKIE,cookie.toString());
//            
//            return ResponseEntity.ok("Login successful");
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtil.extractTokenFromRequest(request);

        if (token != null && jwtUtil.validateToken(token)) {
            authService.logout(token);
        }
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserCredentials user) {
        try {
            authService.register(user);
            return ResponseEntity.status(201).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error Registering User" + e);
        }
    }

    @PostMapping("/registerWithDetails")
    public ResponseEntity<String> registerWithDetails(@RequestBody Map<String, String> userData) {
        try {
            authService.registerWithDetails(userData);
            return ResponseEntity.status(201).body("User registered successfully with employee record");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        try {
            authService.updatePassword(email, oldPassword, newPassword);
            return ResponseEntity.ok("Password Updated Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating password" + e);
        }

    }

    @GetMapping("/{service}/downloadExcel")
    public Mono<ResponseEntity<byte[]>> forwardExcelDownload(
            @PathVariable String service,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value) {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "downloadExcel")
                .queryParamIfPresent("type", Optional.ofNullable(type))
                .queryParamIfPresent("value", Optional.ofNullable(value))
                .build())
                .retrieve()
                .bodyToMono(byte[].class
                )
                .map(body -> {
                    HttpHeaders headers = new HttpHeaders();

                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                    headers.setContentDisposition(ContentDisposition.attachment()
                            .filename("data.xlsx")
                            .build());
                    return new ResponseEntity<>(body, headers, HttpStatus.OK);
                }
                );
    }

    @GetMapping("/getIsAdmin/{email}")
    public ResponseEntity<Boolean> getIsAdmin(@PathVariable String email) {
        return ResponseEntity.ok(authService.getIsAdminByEmail(email));
    }

    @GetMapping("/getEmpIdByEmail/{email}")
    public ResponseEntity<Integer> getEmpidByEmail(@PathVariable String email) {
        return ResponseEntity.ok(authService.getEmployeeIdByEmail(email));
    }

    @GetMapping("/{service}/{endpoint}")
    public Mono<String> forwardGetRequest(
            @PathVariable String service,
            @PathVariable String endpoint) {
        System.out.println("Forwarding request to service: " + service);
        System.out.println("Endpoint: " + endpoint);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, endpoint)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );

    }

    @GetMapping("/{service}/getNames/{name}")
    public Mono<String> forwardGetNamesRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Fetching all matching names for : " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getNames", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getProjects/{name}")
    public Mono<String> forwardGetProjectNamesRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Fetching all matching names for : " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getProjects", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getIds/{name}")
    public Mono<String> forwardGetIdsRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Fetching all matching names for : " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getIds", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getAllocIds/{name}")
    public Mono<String> forwardGetAllocIdsRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Fetching all matching names for : " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getAllocIds", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getAllocDelByEmpName/{name}")
    public Mono<String> getAllocationForDeleteByEmployeeName(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Fetching all matching names for : " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getAllocDelByEmpName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getAllocDelByProjName/{name}")
    public Mono<String> getAllocationForDeleteByProjectName(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Fetching all matching names for : " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getAllocDelByProjName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getById/{id}")
    public Mono<String> forwardGetByIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " ID: " + id);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getById", id)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getEmpIdByName/{name}")
    public Mono<String> forwardGetEmpIdByNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " Name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getEmpIdByName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getProjIdByName/{name}")
    public Mono<String> forwardGetProjIdByNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " Name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getProjIdByName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/updateByProjName/{name}")
    public Mono<String> forwardGetAllocByProjNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " Name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "updateByProjName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getAllocByEmpName/{name}")
    public Mono<String> forwardGetAllByEmpNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for employee name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getAllocByEmpName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getByEmpName/{name}")
    public Mono<String> forwardGetByEmpNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for employee name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getByEmpName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getByProjName/{name}")
    public Mono<String> forwardGetByProjNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for employee name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getByProjName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getByEmpId/{id}")
    public Mono<String> forwardGetByEmpIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " ID: " + id);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getByEmpId", id)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getByProjId/{id}")
    public Mono<String> forwardGetByProjIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " ID: " + id);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getByProjId", id)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getProjIdByEmpId/{id}")
    public Mono<String> forwardGetProjIdByEmpIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " ID: " + id);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getProjIdByEmpId", id)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getAllByName/{name}")
    public Mono<String> forwardGetAllByNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getAllByName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getByName/{name}")
    public Mono<String> forwardGetByNameForUpdateRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " name: " + name);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getByName", name)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @GetMapping("/{service}/getNameByEmail/{email}")
    public Mono<String> forwardGetNameByEmail(
            @PathVariable String service,
            @PathVariable String email) {

        System.out.println("Forwarding GET Request to service: " + service);
        System.out.println("Fetching data for " + service + " email: " + email);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "getNameByEmail", email)
                .build())
                .retrieve()
                .bodyToMono(String.class
                );
    }

    @PostMapping("/{service}/{endpoint}")
    public Mono<ResponseEntity<String>> forwardPostRequest(
            @PathVariable String service,
            @PathVariable String endpoint,
            @RequestBody Map<String, Object> requestBody) {

        System.out.println("Forwarding request to service: " + service);
        System.out.println("Endpoint: " + endpoint);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, endpoint)
                .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @PutMapping("/{service}/updateId/{id}")
    public Mono<ResponseEntity<String>> forwardUpdateByIdRequest(
            @PathVariable String service,
            @PathVariable String id,
            @RequestBody Map<String, Object> requestBody) {
        System.out.println("Forwarding request to service: " + service);
        System.out.println("Updating data for " + service + " ID: " + id);

        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "updateId", id)
                .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @PutMapping("/{service}/updateAllocByProjId/{id}")
    public Mono<ResponseEntity<String>> forwardUpdateAllocByProjIdRequest(
            @PathVariable String service,
            @PathVariable String id,
            @RequestBody Map<String, Object> requestBody) {
        System.out.println("Forwarding request to service: " + service);
        System.out.println("Updating data for " + service + " ID: " + id);

        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "updateAllocByProjId", id)
                .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @PutMapping("/{service}/updateName/{name}")
    public Mono<ResponseEntity<String>> forwardUpdateByNameRequest(
            @PathVariable String service,
            @PathVariable String name,
            @RequestBody Map<String, Object> requestBody) {
        System.out.println("Forwarding request to service: " + service);
        System.out.println("Updating data for " + service + " name: " + name);

        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "updateName", name)
                .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @DeleteMapping("/{service}/deleteId/{id}")
    public Mono<ResponseEntity<String>> forwardDeleteByIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding DELETE Request to service: " + service);
        System.out.println("Deleting data for " + service + " ID: " + id);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "deleteId", id)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @DeleteMapping("/{service}/deleteEmpId/{id}")
    public Mono<ResponseEntity<String>> forwardDeleteByEmpIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding DELETE Request to service: " + service);
        System.out.println("Deleting data for " + service + " ID: " + id);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "deleteEmpId", id)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @DeleteMapping("/{service}/deleteProjId/{id}")
    public Mono<ResponseEntity<String>> forwardDeleteByProjIdRequest(
            @PathVariable String service,
            @PathVariable String id) {

        System.out.println("Forwarding DELETE Request to service: " + service);
        System.out.println("Deleting data for " + service + " ID: " + id);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "deleteProjId", id)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @DeleteMapping("/{service}/deleteName/{name}")
    public Mono<ResponseEntity<String>> forwardDeleteByNameRequest(
            @PathVariable String service,
            @PathVariable String name) {

        System.out.println("Forwarding DELETE Request to service: " + service);
        System.out.println("Deleting data for " + service + " name: " + name);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, "deleteName", name)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    @DeleteMapping("/{service}/{endpoint}")
    public Mono<ResponseEntity<String>> forwardDeleteRequest(
            @PathVariable String service,
            @PathVariable String endpoint) {
        System.out.println("Forwarding request to service: " + service);
        System.out.println("Endpoint: " + endpoint);

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(getPortForService(service))
                .pathSegment(service, endpoint)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class
                        ).flatMap(errorMessage -> {
                            // Handle 500 errors with the message from allocation_service
                            return Mono.error(
                                    new WebClientResponseException(
                                            errorMessage,
                                            response.statusCode().value(),
                                            response.statusCode().getReasonPhrase(),
                                            null, null, null));
                        }
                        )
                )
                .bodyToMono(String.class
                )
                .map(responseBody -> ResponseEntity.ok(responseBody)) // Successful response
                .onErrorResume(WebClientResponseException.class,
                        ex -> {
                            // Return the actual error message from allocation_service with its status code
                            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                                    .body(ex.getMessage()));
                        });
    }

    private int getPortForService(String service) {
        switch (service) {
            case "employee":
                return 8081;
            case "project":
                return 8082;
            case "allocation":
                return 8083;
            default:
                throw new IllegalArgumentException("Invalid service: " + service);
        }
    }

}
