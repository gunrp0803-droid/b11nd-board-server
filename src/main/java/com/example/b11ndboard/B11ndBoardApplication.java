package com.example.b11ndboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class B11ndBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(B11ndBoardApplication.class, args);
    }

}
