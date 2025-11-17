package vn.edu.fpt.pharma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("vn.edu.fpt.pharma.entity")
@SpringBootApplication
public class PharmaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmaApplication.class, args);
    }
}
