package com.example.kenect_labs_api.service;

import com.example.kenect_labs_api.client.ApiClient;
import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.factory.ApiClientFactory;
import com.example.kenect_labs_api.mapper.ContactResponseMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ContactServiceIntegrationTest {

    @Mock
    private ApiClientFactory apiClientFactory;

    @Mock
    private ApiClient apiClient;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private ContactResponseMapper contactResponseMapper;

    private CacheManager cacheManager;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheManager = new ConcurrentMapCacheManager("contactsCache");
        contactService = new ContactService(apiClientFactory, sourceManager, cacheManager, contactResponseMapper);

        when(apiClientFactory.getApiClient("kenect-labs")).thenReturn(apiClient);
        Cache contactsCache = cacheManager.getCache("contactsCache");
        if (contactsCache != null) contactsCache.clear();
    }

    @Test
    void getAllContacts_shouldFetchContactsAndStoreInCache() {
        OffsetDateTime date = OffsetDateTime.parse("2020-06-24T19:37:16.688Z", DateTimeFormatter.ISO_DATE_TIME);
        ContactDTO contact1 = new ContactDTO(1L, "John Doe", "johndoe@example.com", "KENECT_LABS", date, date);
        List<ContactDTO> contacts = List.of(contact1);

        Response response = Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, StandardCharsets.UTF_8, null))
                .status(200)
                .headers(Map.of("Total-Pages", List.of("1")))
                .body("[{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}]", StandardCharsets.UTF_8)
                .build();

        when(apiClient.getContacts(anyInt(), anyString())).thenReturn(response);
        when(contactResponseMapper.mapToContactList(response)).thenReturn(contacts);
        when(sourceManager.getTokenForSource("kenect-labs")).thenReturn("mock-token");

        List<ContactDTO> result = contactService.getAllContacts("kenect-labs");

        assertEquals(contacts, result, "Should return the contacts from API");

        Cache cache = cacheManager.getCache("contactsCache");
        List<ContactDTO> cachedContacts = cache.get("contacts", List.class);
        assertEquals(contacts, cachedContacts, "Cache should store the contacts");

        verify(apiClient, times(1)).getContacts(anyInt(), anyString());
        verify(sourceManager, times(1)).getTokenForSource("kenect-labs");
    }

    @Test
    void getAllContacts_shouldUseFallback_whenApiClientFails() {
        when(apiClient.getContacts(anyInt(), anyString())).thenThrow(new RuntimeException("API error"));
        when(sourceManager.getTokenForSource("kenect-labs")).thenReturn("mock-token");

        OffsetDateTime date = OffsetDateTime.parse("2020-06-24T19:37:16.688Z", DateTimeFormatter.ISO_DATE_TIME);
        ContactDTO contactFallback = new ContactDTO(2L, "Fallback User", "fallback@example.com", "KENECT_LABS", date, date);
        List<ContactDTO> fallbackContacts = List.of(contactFallback);

        Cache cache = cacheManager.getCache("contactsCache");
        if (cache != null) {
            cache.put("kenect-labs", fallbackContacts);
        }

        List<ContactDTO> result = contactService.getAllContacts("kenect-labs");

        assertEquals(fallbackContacts, result, "Should return cached fallback contacts");
        verify(apiClient, times(1)).getContacts(anyInt(), anyString());
    }

    @Test
    void getContactsFromCache_shouldReturnCachedContacts_whenApiFails() {
        OffsetDateTime date = OffsetDateTime.parse("2020-06-24T19:37:16.688Z", DateTimeFormatter.ISO_DATE_TIME);
        ContactDTO cachedContact = new ContactDTO(3L, "Cached Contact", "cached@example.com", "KENECT_LABS", date, date);
        List<ContactDTO> cachedContacts = List.of(cachedContact);

        Cache cache = cacheManager.getCache("contactsCache");
        if (cache != null) {
            cache.put("kenect-labs", cachedContacts);
        }

        List<ContactDTO> result = contactService.getContactsFromCache("kenect-labs");

        assertEquals(cachedContacts, result, "Should return cached contacts");
    }

    @Test
    void getAllContacts_shouldUpdateTotalPagesCorrectly() {
        Response response = Response.builder()
                .request(Request.create(Request.HttpMethod.GET, "", Map.of(), null, StandardCharsets.UTF_8, null))
                .status(200)
                .headers(Map.of("Total-Pages", List.of("5")))
                .body("[]", StandardCharsets.UTF_8)
                .build();

        when(apiClient.getContacts(anyInt(), anyString())).thenReturn(response);
        when(sourceManager.getTokenForSource("kenect-labs")).thenReturn("mock-token");

        contactService.getAllContacts("kenect-labs");

        Cache cache = cacheManager.getCache("contactsCache");
        Integer cachedTotalPages = cache.get("totalPages", Integer.class);
        assertEquals(5, cachedTotalPages, "Should update total pages in cache");

        verify(apiClient, times(1)).getContacts(anyInt(), anyString());
    }
}

