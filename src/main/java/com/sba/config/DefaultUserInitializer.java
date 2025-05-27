package com.sba.config;

import com.sba.entity.User;
import com.sba.enums.UserRole;
import com.sba.repository.AuthenticationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;


    public DefaultUserInitializer(AuthenticationRepository authenticationRepository, PasswordEncoder passwordEncoder) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        if (authenticationRepository.findByEmail("swpproject2024@gmail.com").isEmpty()) {
            User user = User.builder()
                    .password(passwordEncoder.encode("12345"))
                    .role(UserRole.ADMIN)
                    .email("swpproject2024@gmail.com")
                    .name("ADMIN")
                    .enable(true)
                    .deleted(false).build();
            authenticationRepository.saveAndFlush(user);
        }
    }
}
