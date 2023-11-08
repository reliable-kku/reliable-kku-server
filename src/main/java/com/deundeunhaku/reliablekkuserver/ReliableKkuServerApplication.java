package com.deundeunhaku.reliablekkuserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReliableKkuServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReliableKkuServerApplication.class, args);
	}

}
