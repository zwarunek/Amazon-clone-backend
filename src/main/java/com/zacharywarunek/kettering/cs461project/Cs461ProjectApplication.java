package com.zacharywarunek.kettering.cs461project;

import com.zacharywarunek.kettering.cs461project.config.CORSFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Cs461ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(Cs461ProjectApplication.class, args);
    }

}
