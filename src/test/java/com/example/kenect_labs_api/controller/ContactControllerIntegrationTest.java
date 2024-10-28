package com.example.kenect_labs_api.controller;

import com.example.kenect_labs_api.config.SecurityConfig;
import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.service.ContactService;
import com.example.kenect_labs_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class ContactControllerIntegrationTest {

    @MockBean
    private ContactService contactService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(authorities = {"SCOPE_contacts_read"})
    void getAllContacts_shouldReturnContactsList_whenServiceSucceeds() throws Exception {
        OffsetDateTime date = OffsetDateTime.parse("2020-06-24T19:37:16.688Z");
        ContactDTO contact = new ContactDTO(1L, "John Doe", "johndoe@example.com", "KENECT_LABS", date, date);
        List<ContactDTO> contacts = List.of(contact);

        when(contactService.getAllContacts()).thenReturn(contacts);

        mockMvc.perform(get("/contacts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(contactService, times(1)).getAllContacts();
    }

    @Test
    @WithMockUser(authorities = {"SCOPE_contacts_read"})
    void getAllContacts_shouldReturnInternalServerError_whenServiceFails() throws Exception {
        when(contactService.getAllContacts()).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/contacts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(contactService, times(1)).getAllContacts();
    }

    @Test
    void getAllContacts_shouldReturnUnauthorized_whenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/contacts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(contactService, times(0)).getAllContacts();
    }

    @Test
    @WithMockUser(authorities = {"invalid_scope"})
    void getAllContacts_shouldReturnForbidden_whenUserWithoutProperScope() throws Exception {
        mockMvc.perform(get("/contacts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(contactService, times(0)).getAllContacts();
    }
}