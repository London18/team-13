package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.CredentialSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialSetRepository
        extends JpaRepository<CredentialSet, Long> {
    
}
