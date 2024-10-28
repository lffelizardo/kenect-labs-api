package com.example.kenect_labs_api.factory;

import com.example.kenect_labs_api.client.ApiClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiClientFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final Map<String, ApiClient> apiClients = new HashMap<>();
    private final Map<String, ApiClient> apiClientCache = new HashMap<>();
    private boolean initialized = false;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApiClient getApiClient(String source) {
        initializeApiClientsIfNeeded();
        if (apiClientCache.containsKey(source)) {
            return apiClientCache.get(source);
        }
        ApiClient client = apiClients.get(source);
        if (client == null) {
            throw new IllegalArgumentException("Source not supported: " + source);
        }

        apiClientCache.put(source, client);
        return client;
    }

    private synchronized void initializeApiClientsIfNeeded() {
        if (!initialized) {
            Map<String, ApiClient> beansOfType = applicationContext.getBeansOfType(ApiClient.class);
            for (Map.Entry<String, ApiClient> entry : beansOfType.entrySet()) {
                String source = entry.getValue().getSource().toLowerCase();
                apiClients.put(source, entry.getValue());
            }
            initialized = true;
        }
    }
}