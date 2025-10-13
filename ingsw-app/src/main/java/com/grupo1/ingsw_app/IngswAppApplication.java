package com.grupo1.ingsw_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class IngswAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngswAppApplication.class, args);
	}

}
