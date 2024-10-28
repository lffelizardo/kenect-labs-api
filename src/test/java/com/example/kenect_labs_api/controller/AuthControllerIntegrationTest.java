package com.example.kenect_labs_api.controller;

import com.example.kenect_labs_api.config.JwtTokenProvider;
import com.example.kenect_labs_api.config.SecurityConfig;
import com.example.kenect_labs_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void login_shouldReturnToken_whenAuthenticationSucceeds() throws Exception {

        String username = "testuser";
        String password = "testpass";
        String token = "mock-jwt-token";

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new User(username, password, Collections.emptyList()), null, Collections.emptyList()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(anyString(), anyCollection(), anyString())).thenReturn(token);

        String requestBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(anyString(), anyCollection(), anyString());
    }

    @Test
    void login_shouldReturnUnauthorized_whenAuthenticationFails() throws Exception {
        // Arrange
        String username = "invaliduser";
        String password = "invalidpass";

        // Mocking failed authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {
                });

        String requestBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        // Act and Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(0)).generateToken(anyString(), anyCollection(), anyString());
    }
}