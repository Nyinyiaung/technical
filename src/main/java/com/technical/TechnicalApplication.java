package com.technical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TechnicalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechnicalApplication.class, args);
	}

}
