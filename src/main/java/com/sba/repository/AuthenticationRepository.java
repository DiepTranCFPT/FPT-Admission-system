package com.sba.repository;

import com.sba.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuthenticationRepository extends JpaRepository<User, String> {   // dua ra daatabase


    Optional<User> findByPhone(String numer);




    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);



    Optional<User> findById(String id);

    Optional<User> findByFirebaseUid(String firebase);



}
