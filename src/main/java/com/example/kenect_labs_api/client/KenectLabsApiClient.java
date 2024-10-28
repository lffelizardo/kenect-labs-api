package com.example.kenect_labs_api.client;

import com.example.kenect_labs_api.config.FeignConfig;
import feign.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kenectLabsClient", url = "${api.sources.kenect-labs.url}", configuration = FeignConfig.class)
@Qualifier("KENECT-LABS")
public interface KenectLabsApiClient extends ApiClient {

    @GetMapping("/contacts")
    Response getContacts(
            @RequestParam("page") int page,
            @RequestHeader("Authorization") String token
    );

    @Override
    default String getSource() {
        return "KENECT-LABS";
    }
}
