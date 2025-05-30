package com.sba.configs;

import com.sba.accounts.pojos.Accounts;
import com.sba.enums.UserRole;
import com.sba.authentications.repositories.AuthenticationRepository;
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
            Accounts accounts = Accounts.builder()
                    .password(passwordEncoder.encode("12345"))
                    .role(UserRole.ADMIN)
                    .email("swpproject2024@gmail.com")
                    .name("ADMIN")
                    .enable(true)
                    .deleted(false).build();
            authenticationRepository.saveAndFlush(accounts);
        }
    }
}
