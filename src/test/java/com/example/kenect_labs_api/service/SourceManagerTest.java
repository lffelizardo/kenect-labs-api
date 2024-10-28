package com.example.kenect_labs_api.service;

import com.example.kenect_labs_api.config.ApiSourcesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SourceManagerTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ApiSourcesConfig apiSourcesConfig;

    @InjectMocks
    private SourceManager sourceManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSources_shouldReturnAllSourceNames_whenSourcesAreConfigured() {
        Map<String, ApiSourcesConfig.SourceProperties> sources = new HashMap<>();
        sources.put("source1", new ApiSourcesConfig.SourceProperties("url", "token1","source1_name"));
        sources.put("source2", new ApiSourcesConfig.SourceProperties("url", "token2","source2_name"));
        when(apiSourcesConfig.getSources()).thenReturn(sources);

        List<String> result = sourceManager.getAllSources();

        assertEquals(List.of("source1", "source2"), result);
        verify(apiSourcesConfig, times(1)).getSources();
    }

    @Test
    void isCompleteCache_shouldReturnTrue_whenCacheIsComplete() {
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("contactsCache")).thenReturn(mockCache);
        when(mockCache.get("source1_lastPageFetched", Integer.class)).thenReturn(5);
        when(mockCache.get("source1_totalPages", Integer.class)).thenReturn(5);

        boolean result = sourceManager.isCompleteCache("source1");

        assertTrue(result);
        verify(cacheManager, times(2)).getCache("contactsCache");
        verify(mockCache, times(1)).get("source1_lastPageFetched", Integer.class);
        verify(mockCache, times(1)).get("source1_totalPages", Integer.class);
    }

    @Test
    void isCompleteCache_shouldReturnFalse_whenCacheIsIncomplete() {
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("contactsCache")).thenReturn(mockCache);
        when(mockCache.get("source1_lastPageFetched", Integer.class)).thenReturn(3);
        when(mockCache.get("source1_totalPages", Integer.class)).thenReturn(5);

        boolean result = sourceManager.isCompleteCache("source1");

        assertFalse(result);
        verify(cacheManager, times(2)).getCache("contactsCache");
        verify(mockCache, times(1)).get("source1_lastPageFetched", Integer.class);
        verify(mockCache, times(1)).get("source1_totalPages", Integer.class);
    }

    @Test
    void isCompleteCache_shouldReturnFalse_whenCacheEntriesAreNull() {
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("contactsCache")).thenReturn(mockCache);
        when(mockCache.get("source1_lastPageFetched", Integer.class)).thenReturn(null);
        when(mockCache.get("source1_totalPages", Integer.class)).thenReturn(null);

        boolean result = sourceManager.isCompleteCache("source1");

        assertFalse(result);
        verify(cacheManager, times(2)).getCache("contactsCache");
        verify(mockCache, times(1)).get("source1_lastPageFetched", Integer.class);
        verify(mockCache, times(1)).get("source1_totalPages", Integer.class);
    }

    @Test
    void getTokenForSource_shouldReturnBearerToken_whenSourceExists() {
        Map<String, ApiSourcesConfig.SourceProperties> sources = new HashMap<>();
        sources.put("source1", new ApiSourcesConfig.SourceProperties("url", "token1","source1_name"));
        when(apiSourcesConfig.getSources()).thenReturn(sources);

        String token = sourceManager.getTokenForSource("source1");

        assertEquals("Bearer token1", token);
        verify(apiSourcesConfig, times(1)).getSources();
    }

    @Test
    void getSourceName_shouldReturnSourceName_whenSourceExists() {
        Map<String, ApiSourcesConfig.SourceProperties> sources = new HashMap<>();
        sources.put("source1", new ApiSourcesConfig.SourceProperties("url", "token1","source1_name"));
        when(apiSourcesConfig.getSources()).thenReturn(sources);

        String sourceName = sourceManager.getSourceName("source1");

        assertEquals("source1_name", sourceName);
        verify(apiSourcesConfig, times(1)).getSources();
    }

    @Test
    void getTokenForSource_shouldThrowException_whenSourceDoesNotExist() {
        Map<String, ApiSourcesConfig.SourceProperties> sources = new HashMap<>();
        when(apiSourcesConfig.getSources()).thenReturn(sources);

        assertThrows(NullPointerException.class, () -> sourceManager.getTokenForSource("source1"));
        verify(apiSourcesConfig, times(1)).getSources();
    }

    @Test
    void getSourceName_shouldThrowException_whenSourceDoesNotExist() {
        Map<String, ApiSourcesConfig.SourceProperties> sources = new HashMap<>();
        when(apiSourcesConfig.getSources()).thenReturn(sources);

        assertThrows(NullPointerException.class, () -> sourceManager.getSourceName("source1"));
        verify(apiSourcesConfig, times(1)).getSources();
    }
}
