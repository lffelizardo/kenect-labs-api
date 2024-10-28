package com.example.kenect_labs_api.service;

import com.example.kenect_labs_api.config.ApiSourcesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SourceManager {

    private final CacheManager cacheManager;
    private final ApiSourcesConfig apiSourcesConfig;

    @Autowired
    public SourceManager(CacheManager cacheManager, ApiSourcesConfig apiSourcesConfig) {
        this.cacheManager = cacheManager;
        this.apiSourcesConfig = apiSourcesConfig;
    }

    public List<String> getAllSources() {
        return apiSourcesConfig.getSources().keySet().stream().collect(Collectors.toList());
    }

    public boolean isCompleteCache(String source) {
        Integer lastPageFetched = cacheManager.getCache("contactsCache")
                .get(source + "_lastPageFetched", Integer.class);
        Integer totalPages = cacheManager.getCache("contactsCache")
                .get(source + "_totalPages", Integer.class);
        return lastPageFetched != null && totalPages != null && lastPageFetched >= totalPages;
    }

    public String getTokenForSource(String source) {
        return "Bearer " + apiSourcesConfig.getSources().get(source).getToken();
    }

    public String getSourceName(String source){
        return apiSourcesConfig.getSources().get(source).getName();
    }

}

