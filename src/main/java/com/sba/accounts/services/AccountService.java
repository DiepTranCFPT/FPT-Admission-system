package com.sba.accounts.services;

import com.sba.accounts.pojos.Accounts;
import com.sba.accounts.pojos.AccountUpdateRequest;
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
    private AccountUtils accountUtils;

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

    public Accounts updateAccount(String id, AccountUpdateRequest req) {
        Accounts acc;
        if (id == null) {
            acc = accountUtils.getCurrentUser();
        } else {
            acc = authenticationRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        }
        if (req.getUsername() != null) acc.setUsername(req.getUsername());
        if (req.getPhoneNumber() != null) acc.setPhoneNumber(req.getPhoneNumber());
        if (req.getEmail() != null) acc.setEmail(req.getEmail());
        if (req.getRole() != null) acc.setRole(req.getRole());
        if (req.getEnable() != null) acc.setEnable(req.getEnable());
        return authenticationRepository.saveAndFlush(acc);
    }

    public Accounts getCurrentUser() {
        return accountUtils.getCurrentUser();
    }
    public Accounts getById(String id) {
        return authenticationRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }
    public java.util.List<Accounts> getAll() {
        return authenticationRepository.findAll();
    }
}
