package com.sba.applications.repository;

import com.sba.accounts.pojos.Accounts;
import com.sba.applications.pojos.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
    Optional<Application> findByAccounts(Accounts accounts);


}
