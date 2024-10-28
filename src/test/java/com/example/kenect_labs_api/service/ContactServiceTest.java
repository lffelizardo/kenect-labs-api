package com.example.kenect_labs_api.service;

import com.example.kenect_labs_api.client.ApiClient;
import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.factory.ApiClientFactory;
import com.example.kenect_labs_api.mapper.ContactResponseMapper;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;

    @Mock
    private ApiClientFactory apiClientFactory;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ContactResponseMapper contactResponseMapper;

    @Mock
    private ApiClient apiClient;

    @Mock
    private Cache cache;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("contactsCache")).thenReturn(cache);
    }

    @Test
    public void testGetAllContacts_Success() {
        // Arrange
        String source = "kenect-labs";
        when(sourceManager.getTokenForSource(source)).thenReturn("token");
        when(apiClientFactory.getApiClient(source)).thenReturn(apiClient);
        when(cache.get("totalPages", Integer.class)).thenReturn(1);
        when(cache.get("lastPageFetched", Integer.class)).thenReturn(0);

        List<ContactDTO> mockContacts = new ArrayList<>();
        mockContacts.add(new ContactDTO()); // Add a mock contact
        Response response = mock(Response.class);
        when(apiClient.getContacts(1, "token")).thenReturn(response);
        when(contactResponseMapper.mapToContactList(response)).thenReturn(mockContacts);
        when(response.headers()).thenReturn(Map.of("Total-Pages", List.of("1")));

        // Act
        List<ContactDTO> contacts = contactService.getAllContacts(source);

        // Assert
        assertNotNull(contacts);
        assertEquals(1, contacts.size());
        verify(cache, times(2)).put("totalPages", 1);
    }

    @Test
    public void testGetAllContacts_Fallback() {
        // Arrange
        String source = "kenect-labs";
        when(sourceManager.getTokenForSource(source)).thenReturn("token");
        when(apiClientFactory.getApiClient(source)).thenReturn(apiClient);
        when(cache.get("totalPages", Integer.class)).thenReturn(1);
        when(cache.get("lastPageFetched", Integer.class)).thenReturn(0);

        when(apiClient.getContacts(1, "token")).thenThrow(new RuntimeException("Service error"));
        when(cache.get(source, List.class)).thenReturn(new ArrayList<>()); // Simulate cache fallback

        // Act
        List<ContactDTO> contacts = contactService.getAllContacts(source);

        // Assert
        assertNotNull(contacts);
        assertTrue(contacts.isEmpty());
        verify(cache, times(1)).get(source, List.class);
    }
}