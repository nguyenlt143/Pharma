package vn.edu.fpt.pharma;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.pharma.constant.UserRole;
import vn.edu.fpt.pharma.entity.Store;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.StoreRepository;
import vn.edu.fpt.pharma.repository.UserRepository;

import java.util.UUID;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class PharmaApplication implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(PharmaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        User user = new User();
        user.setRole(UserRole.SYSTEM_ADMIN);
        user.setFullName("LE TUNG NGUYEN");
        user.setStoreCode("STORE001");
        user.setEmail("nguyen");
        user.setPassword(passwordEncoder.encode("nguyen.le"));
        userRepository.save(user);

        for (int i = 1; i <= 100; i++) {
            Store store = new Store();
            store.setStoreCode("STORE" + String.format("%03d", i));
            store.setStoreName("Store " + i);
            store.setAddress("Street " + i + ", City, Country Street 2, City, Country Street 2, City, Country");
            store.setAddressCode("ADDR" + i);
            store.setPhoneNumber("+12345678" + String.format("%02d", i));
            store.setAddressCode("00004");
            store.setLatitude(10.762622 + i * 0.001);
            store.setLongitude(106.660172 + i * 0.001);
            storeRepository.save(store);
        }
    }
}
