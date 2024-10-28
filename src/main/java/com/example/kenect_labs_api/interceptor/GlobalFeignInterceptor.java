package com.example.kenect_labs_api.interceptor;

import com.example.kenect_labs_api.config.ApiSourcesConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GlobalFeignInterceptor implements RequestInterceptor {

    private final ApiSourcesConfig apiSourcesConfig;

    @Autowired
    public GlobalFeignInterceptor(ApiSourcesConfig apiSourcesConfig) {
        this.apiSourcesConfig = apiSourcesConfig;
    }

    @Override
    public void apply(RequestTemplate template) {

        String targetUrl = ((Target.HardCodedTarget<?>) template.feignTarget()).url();
        apiSourcesConfig.getSources().entrySet().stream()
                .filter(entry -> targetUrl.equals(entry.getValue().getUrl()))
                .findFirst()
                .ifPresent(entry -> {
                    String token = entry.getValue().getToken();
                    template.header("Authorization", "Bearer " + token);
                });
    }
}
