package com.sba.authentications.repositories;

import com.sba.accounts.pojos.Accounts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuthenticationRepository extends JpaRepository<Accounts, String> {


    Optional<Accounts> findByPhoneNumber(String numer);

    boolean existsByEmail(String email);

    Optional<Accounts> findByEmail(String email);



    Optional<Accounts> findById(String id);

    Optional<Accounts> findByFirebaseUid(String firebase);



}
