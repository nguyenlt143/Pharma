package vn.edu.fpt.pharma;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.pharma.entity.Store;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.StoreRepository;
import vn.edu.fpt.pharma.repository.UserRepository;

import java.util.Arrays;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class PharmaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmaApplication.class, args);
    }

}
