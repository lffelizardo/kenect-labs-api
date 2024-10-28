package com.example.kenect_labs_api.task;

import com.example.kenect_labs_api.service.ContactService;
import com.example.kenect_labs_api.service.SourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheUpdateTask {

    private final SourceManager sourceManager;
    private final ContactService contactService;

    @Autowired
    public CacheUpdateTask(SourceManager sourceManager, ContactService contactService) {
        this.sourceManager = sourceManager;
        this.contactService = contactService;
    }

    @Scheduled(fixedRate = 6000000)
    public void updateCacheIfIncomplete() {
        for (String source : sourceManager.getAllSources()) {
            if (!sourceManager.isCompleteCache(source)) {
                contactService.getAllContacts(source);
            }
        }
    }
}

