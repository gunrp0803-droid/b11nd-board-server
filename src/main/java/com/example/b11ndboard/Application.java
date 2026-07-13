package com.example.b11ndboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(Application.class, args);
    }

    private static void loadEnv() {
        try {
            if (Files.exists(Paths.get(".env"))) {
                List<String> lines = Files.readAllLines(Paths.get(".env"));
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            System.setProperty(key, value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load .env file: " + e.getMessage());
        }
    }
}
