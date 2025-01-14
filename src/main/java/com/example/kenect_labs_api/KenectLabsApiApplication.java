package com.example.kenect_labs_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.kenect_labs_api.client")
@EnableCaching
@EnableScheduling
public class KenectLabsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KenectLabsApiApplication.class, args);
	}

}
