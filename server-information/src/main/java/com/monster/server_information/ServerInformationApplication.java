package com.monster.server_information;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = {"controller","infraestrucutre", "Security","Mappers", "application"})
@EnableR2dbcRepositories(basePackages = "infraestrucutre")
public class ServerInformationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerInformationApplication.class, args);
	}

}