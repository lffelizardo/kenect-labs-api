package com.example.kenect_labs_api.dto;

import java.util.List;

public class ContactResponseDTO {

    private List<ContactDTO> contacts;

    public List<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}
