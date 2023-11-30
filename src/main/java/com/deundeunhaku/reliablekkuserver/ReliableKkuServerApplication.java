package com.deundeunhaku.reliablekkuserver;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReliableKkuServerApplication {

  @PostConstruct
  public void started() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
  }
  public static void main(String[] args) {
    SpringApplication.run(ReliableKkuServerApplication.class, args);
  }

}
