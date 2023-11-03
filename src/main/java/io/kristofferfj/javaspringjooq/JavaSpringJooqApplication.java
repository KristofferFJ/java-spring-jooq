package io.kristofferfj.javaspringjooq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JavaSpringJooqApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaSpringJooqApplication.class, args);
    }

}
