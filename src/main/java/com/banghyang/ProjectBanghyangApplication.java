package com.banghyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan // @ConfigurationProperties 사용을 위한 의존성 추가
public class ProjectBanghyangApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBanghyangApplication.class, args);
    }

}
