package com.example.kenect_labs_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "api")
public class ApiSourcesConfig {

    private Map<String, SourceProperties> sources;

    public Map<String, SourceProperties> getSources() {
        return sources;
    }

    public void setSources(Map<String, SourceProperties> sources) {
        this.sources = sources;
    }

    public static class SourceProperties {
        private String url;
        private String token;
        private String name;

        public SourceProperties(){}

        public SourceProperties(String url, String token, String name) {
            this.url = url;
            this.token = token;
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
