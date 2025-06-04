package com.sba.accounts.services;

import com.sba.accounts.pojos.Accounts;
import com.sba.authentications.pojos.AccountDetails;
import com.sba.authentications.repositories.AuthenticationRepository;
import com.sba.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public AccountService(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Accounts accounts;
        if (AccountUtils.isValidEmail(username)) {
            accounts = authenticationRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("email is not a valid!"));
        } else if (AccountUtils.isValidPhoneNumber(username)) {
            accounts = authenticationRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Phone number is not a valid!"));
        } else {
            throw new UsernameNotFoundException("Account does not exist!");
        }
        return new AccountDetails(accounts);
    }
    public Accounts getUserById(String id) {
        return authenticationRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }
}
