package com.grupoG.ProyectoSIG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectoSigApplication {

	public static void main(String[] args) {
		System.out.println("DATABASE_URL: " + System.getenv("DATABASE_URL"));
		SpringApplication.run(ProyectoSigApplication.class, args);
	}

}
