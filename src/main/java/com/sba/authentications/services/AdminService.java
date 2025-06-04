package com.sba.authentications.services;

import com.sba.accounts.pojos.Accounts;
import com.sba.enums.Roles;
import com.sba.authentications.repositories.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    public void createAdmin(){
        Accounts newAccounts = new Accounts();
        newAccounts.setUsername("admin");
        newAccounts.setPassword("admin");
        newAccounts.setEnable(true);
        newAccounts.setEmail("admin@gmail.com");
        newAccounts.setRole(Roles.ADMIN);
        authenticationRepository.save(newAccounts);
    }
}
