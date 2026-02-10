package com.example.rest_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class RestServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(RestServiceApplication.class);

	@PostConstruct
	public void logStartup() {
		LOG.info("RestServiceApplication started. Controller scanning should be active.");
	}
	public static void main(String[] args) {
		SpringApplication.run(RestServiceApplication.class, args);
	}

}
