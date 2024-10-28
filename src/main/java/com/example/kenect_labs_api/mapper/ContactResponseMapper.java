package com.example.kenect_labs_api.mapper;

import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.dto.ContactResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ContactResponseMapper {

    private final ObjectMapper objectMapper;

    public ContactResponseMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ContactDTO> mapToContactList(Response response) {
        try {
            ContactResponseDTO responseDTO = objectMapper.readValue(
                    new BufferedReader(new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8)),
                    ContactResponseDTO.class
            );
            return responseDTO.getContacts();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar a resposta", e);
        }
    }

    public int getTotalPages(Response response) {
        Map<String, Collection<String>> headers = response.headers();
        String totalPagesHeader = headers.get("Total-Pages").stream().findFirst().get();
        return totalPagesHeader != null ? Integer.parseInt(totalPagesHeader) : 1;
    }
}

