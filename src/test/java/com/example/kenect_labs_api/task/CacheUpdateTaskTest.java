package com.example.kenect_labs_api.task;

import com.example.kenect_labs_api.config.ApiSourcesConfig;
import com.example.kenect_labs_api.service.ContactService;
import com.example.kenect_labs_api.service.SourceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CacheUpdateTaskTest {

    @Mock
    private SourceManager sourceManager;

    @Mock
    private CacheManager cacheManager;
    @Mock
    private ApiSourcesConfig apiSourcesConfig;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private CacheUpdateTask cacheUpdateTask;


    @Test
    void updateCacheIfIncomplete_shouldCallGetAllContactsForIncompleteCaches() {
        List<String> sources = List.of("source1", "source2", "source3");

        when(sourceManager.getAllSources()).thenReturn(sources);
        when(sourceManager.isCompleteCache("source1")).thenReturn(false);
        when(sourceManager.isCompleteCache("source2")).thenReturn(true);
        when(sourceManager.isCompleteCache("source3")).thenReturn(false);

        cacheUpdateTask.updateCacheIfIncomplete();

        verify(contactService, times(1)).getAllContacts("source1");
        verify(contactService, times(1)).getAllContacts("source3");
        verify(contactService, never()).getAllContacts("source2");
    }

    @Test
    void updateCacheIfIncomplete_shouldNotCallGetAllContactsIfAllCachesComplete() {
        List<String> sources = List.of("source1", "source2");

        when(sourceManager.getAllSources()).thenReturn(sources);
        when(sourceManager.isCompleteCache(anyString())).thenReturn(true);

        cacheUpdateTask.updateCacheIfIncomplete();

        verify(contactService, never()).getAllContacts(anyString());
    }

    @Test
    void updateCacheIfIncomplete_shouldHandleExceptionInIsCompleteCache() {
        List<String> sources = List.of("source1", "source2");

        when(sourceManager.getAllSources()).thenReturn(sources);

        doThrow(new RuntimeException("Error checking cache completeness")).when(sourceManager).isCompleteCache("source1");

        Assertions.assertThrows(RuntimeException.class, () -> {cacheUpdateTask.updateCacheIfIncomplete();});

        verify(contactService, never()).getAllContacts("source1");
    }
}