package com.sba.service;

import com.sba.entity.AccountDetails;
import com.sba.entity.User;
import com.sba.repository.AuthenticationRepository;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public UserService(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (AccountUtils.isValidEmail(username)) {
            user = authenticationRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("email is not a valid!"));
        } else if (AccountUtils.isValidPhoneNumber(username)) {
            user = authenticationRepository.findByPhone(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Phone number is not a valid!"));
        } else {
            throw new UsernameNotFoundException("Account does not exist!");
        }
        return new AccountDetails(user);
    }
    public User getUserById(String id) {
        return authenticationRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
