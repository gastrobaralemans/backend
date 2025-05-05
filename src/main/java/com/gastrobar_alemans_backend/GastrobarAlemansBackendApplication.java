package com.gastrobar_alemans_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class GastrobarAlemansBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GastrobarAlemansBackendApplication.class, args);
    }

}
