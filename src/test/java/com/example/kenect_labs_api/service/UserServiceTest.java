package com.example.kenect_labs_api.service;

import com.example.kenect_labs_api.model.User;
import com.example.kenect_labs_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setRole("USER");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("testpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("nonexistentuser"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
    }
}
