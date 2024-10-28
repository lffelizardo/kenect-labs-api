package com.example.kenect_labs_api.controller;

import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@Slf4j
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Operation(summary = "Get all contacts", description = "Retrieve a list of all contacts from the external API, with pagination handled and returned in a common model.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal server error when retrieving contacts")
    })
    @PreAuthorize("hasAuthority('SCOPE_contacts_read')")
    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        try {
            List<ContactDTO> contacts = contactService.getAllContacts();
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("Error retrieving contacts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
