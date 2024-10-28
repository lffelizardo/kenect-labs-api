package com.example.kenect_labs_api.client;

import feign.Response;

public interface ApiClient {
    Response getContacts(int page, String token);
    default String getSource() {
        return "DEFAULT";
    }
}
