package com.sourceallies.loom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class LoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoomApplication.class, args);
	}

	@Bean
	RestClient restClient() {
		return RestClient.create();
	}
}
