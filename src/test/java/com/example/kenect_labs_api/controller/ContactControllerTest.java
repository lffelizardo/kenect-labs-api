package com.example.kenect_labs_api.controller;

import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ContactControllerTest {

    @InjectMocks
    private ContactController contactController;

    @Mock
    private ContactService contactService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllContacts_Success() {

        List<ContactDTO> mockContacts = new ArrayList<>();
        mockContacts.add(new ContactDTO());
        when(contactService.getAllContacts()).thenReturn(mockContacts);


        ResponseEntity<List<ContactDTO>> response = contactController.getAllContacts();


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockContacts, response.getBody());
    }

    @Test
    public void testGetAllContacts_Error() {

        when(contactService.getAllContacts()).thenThrow(new RuntimeException("Service error"));


        ResponseEntity<List<ContactDTO>> response = contactController.getAllContacts();


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}
