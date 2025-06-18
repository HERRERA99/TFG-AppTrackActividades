package com.aitor.api_tfg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiTfgApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTfgApplication.class, args);
	}

}
