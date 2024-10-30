package com.monster.eurecka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EureckaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EureckaApplication.class, args);
	}

}
