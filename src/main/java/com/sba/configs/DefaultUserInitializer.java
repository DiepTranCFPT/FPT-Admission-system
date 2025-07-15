package com.sba.configs;

import com.sba.accounts.pojos.Accounts;
import com.sba.enums.Roles;
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
        // Check if admin user exists by email
        if (authenticationRepository.findByEmail("trancaodiepnct@gmail.com").isEmpty()) {
            try {
                Accounts accounts = Accounts.builder()
                        .password(passwordEncoder.encode("12345"))
                        .role(Roles.ADMIN)
                        .email("trancaodiepnct@gmail.com")
                        .username("ADMIN")
                        .enable(true)
                        .deleted(false).build();
                authenticationRepository.saveAndFlush(accounts);
            } catch (Exception e) {
                // If there's a constraint violation, the user already exists
                System.out.println("Admin user already exists or constraint violation: " + e.getMessage());
            }
        }
    }

    



//    //fix    public void setAuthenticationRepository(AuthenticationRepository authenticationRepository) {
//        this.authenticationRepository = authenticationRepository;

}
