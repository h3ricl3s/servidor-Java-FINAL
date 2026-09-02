package com.labanta.servidorlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class ServidorlocalApplication {



	@Bean
	public RestTemplate restTemplat(){
		return new RestTemplate();
	}


	public static void main(String[] args) {
		SpringApplication.run(ServidorlocalApplication.class, args);
	}

}
