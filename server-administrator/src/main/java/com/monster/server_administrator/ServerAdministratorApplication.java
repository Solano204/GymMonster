package com.monster.server_administrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages = {"controller","infraestrucutre", "Security","Mappers"})
public class ServerAdministratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerAdministratorApplication.class, args);
	}

}
