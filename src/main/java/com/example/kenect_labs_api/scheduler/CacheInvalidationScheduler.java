package com.example.kenect_labs_api.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationScheduler {

    private final CacheManager cacheManager;

    @Autowired
    public CacheInvalidationScheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedRate = 10800)
    public void invalidateAllCaches() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }
}

