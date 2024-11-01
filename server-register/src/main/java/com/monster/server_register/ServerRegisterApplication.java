package com.monster.server_register;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication(scanBasePackages = {"controller","infraestrucutre", "Security","Mappers","Validations"})
@EnableR2dbcRepositories(basePackages = "infraestrucutre")
public class ServerRegisterApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(ServerRegisterApplication.class, args);
	}

}
