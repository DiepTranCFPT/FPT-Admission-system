package com.sba.service;

import com.sba.entity.User;
import com.sba.enums.UserRole;
import com.sba.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    public void createAdmin(){
        User newUser = new User();
        newUser.setName("admin");
        newUser.setPassword("admin");
        newUser.setEnable(true);
        newUser.setEmail("admin@gmail.com");
        newUser.setRole(UserRole.ADMIN);
        authenticationRepository.save(newUser);
    }
}
