package com.juliashouse.sweetpotatoes.repository;

import com.juliashouse.sweetpotatoes.entity.CredentialSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialSetRepository
        extends JpaRepository<CredentialSet, Long> {
    
}
