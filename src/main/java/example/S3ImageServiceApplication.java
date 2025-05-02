package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class S3ImageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3ImageServiceApplication.class, args);
    }

}
