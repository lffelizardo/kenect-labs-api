package com.example.kenect_labs_api.service;

import com.example.kenect_labs_api.client.ApiClient;
import com.example.kenect_labs_api.dto.ContactDTO;
import com.example.kenect_labs_api.factory.ApiClientFactory;
import com.example.kenect_labs_api.mapper.ContactResponseMapper;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ContactService {

    private static final String TOTAL_PAGES_CACHE_KEY = "totalPages";
    private static final String LAST_PAGE_FETCHED_CACHE_KEY = "lastPageFetched";
    private static final String DEFAULT_SOURCE = "kenect-labs";
    private static final String CONTACTS_CACHE_NAME = "contactsCache";

    private final ApiClientFactory apiClientFactory;
    private final SourceManager sourceManager;
    private final CacheManager cacheManager;
    private final ContactResponseMapper contactResponseMapper;

    @Autowired
    public ContactService(ApiClientFactory apiClientFactory, SourceManager sourceManager, CacheManager cacheManager, ContactResponseMapper contactResponseMapper) {
        this.apiClientFactory = apiClientFactory;
        this.sourceManager = sourceManager;
        this.cacheManager = cacheManager;
        this.contactResponseMapper = contactResponseMapper;
    }

    public List<ContactDTO> getAllContacts() {
        return getAllContacts(DEFAULT_SOURCE);
    }

    @Cacheable(value = CONTACTS_CACHE_NAME, key = "#source")
    public List<ContactDTO> getAllContacts(String source) {

        try{
            return fetchContactsFromExternalApi(source);
        }catch (Exception e){
            log.warn("Error fetching contacts from API. Fallback to cache due to: {}", e.getMessage());
            return getContactsFromCache(source);
        }
    }

    private List<ContactDTO> fetchContactsFromExternalApi(String source) {
        ApiClient apiClient = apiClientFactory.getApiClient(source);
        String token = sourceManager.getTokenForSource(source);

        List<ContactDTO> allContacts = new ArrayList<>();
        int currentPage = getLastPageFetched() + 1;
        int totalPages = getTotalPages(source);

        while (currentPage <= totalPages) {
            List<ContactDTO> pageContacts = fetchContacts(apiClient, currentPage, token);
            if (pageContacts.isEmpty()) {
                break;
            }
            allContacts.addAll(pageContacts);
            updateContactSource(allContacts, source);
            saveContactsToCache(allContacts, currentPage - 1, totalPages);
            currentPage++;
        }

        return allContacts;
    }

    public List<ContactDTO> getContactsFromCache(String source) {
        Cache contactsCache = cacheManager.getCache("contactsCache");
        if (contactsCache != null) {
            List<ContactDTO> cachedContacts = contactsCache.get(source, List.class);
            if (cachedContacts != null) {
                return cachedContacts;
            }
        }
        log.error("No cached data available for fallback. Returning empty list.");
        return List.of();
    }

    private List<ContactDTO> fetchContacts(ApiClient apiClient, int currentPage, String token) {

        Response response = apiClient.getContacts(currentPage, token);
        List<ContactDTO> pageContacts = contactResponseMapper.mapToContactList(response);
        updateTotalPages(response);
        return pageContacts;
    }

    private void updateTotalPages(Response response) {
        Optional<String> totalPagesHeader = response.headers().get("Total-Pages").stream().findFirst();
        if (totalPagesHeader.isPresent()) {
            try {
                int totalPages = Integer.parseInt(totalPagesHeader.get());
                cacheManager.getCache(CONTACTS_CACHE_NAME).put(TOTAL_PAGES_CACHE_KEY, totalPages);
            } catch (NumberFormatException e) {
                log.warn("Invalid total pages format: {}", totalPagesHeader.get());
            }
        }
    }

    private void updateContactSource(List<ContactDTO> contacts, String source) {
        String sourceName = sourceManager.getSourceName(source);
        contacts.forEach(contactDTO -> contactDTO.setSource(sourceName));
    }

    private void saveContactsToCache(List<ContactDTO> contacts, int lastPageFetched, int totalPages) {
        var cache = cacheManager.getCache(CONTACTS_CACHE_NAME);
        cache.put("contacts", contacts);
        cache.put(LAST_PAGE_FETCHED_CACHE_KEY, lastPageFetched);
        cache.put(TOTAL_PAGES_CACHE_KEY, totalPages);
    }

    private int getLastPageFetched() {
        Integer lastPageFetched = cacheManager.getCache(CONTACTS_CACHE_NAME).get(LAST_PAGE_FETCHED_CACHE_KEY, Integer.class);
        return lastPageFetched != null ? lastPageFetched : 0;
    }

    private int getTotalPages(String source) {
        Integer totalPages = cacheManager.getCache(CONTACTS_CACHE_NAME).get(TOTAL_PAGES_CACHE_KEY, Integer.class);
        return (totalPages != null) ? totalPages : 1;
    }

    private int fetchAndSetTotalPages(String source) {
        ApiClient apiClient = apiClientFactory.getApiClient(source);
        String token = sourceManager.getTokenForSource(source);

        try {
            var response = apiClient.getContacts(1, token);
            Optional<String> totalPagesHeader = response.headers().get("Total-Pages").stream().findFirst();
            if (totalPagesHeader.isPresent()) {
                try {
                    int totalPages = Integer.parseInt(totalPagesHeader.get());
                    cacheManager.getCache(CONTACTS_CACHE_NAME).put(TOTAL_PAGES_CACHE_KEY, totalPages);
                    return totalPages;
                } catch (NumberFormatException e) {
                    log.warn("Invalid total pages format: {}", totalPagesHeader.get());
                }
            }
            return Integer.MAX_VALUE;
        } catch (Exception e) {
            log.warn("Error fetching contacts: {}", e.getMessage());
            return Integer.MAX_VALUE;
        }
    }
}