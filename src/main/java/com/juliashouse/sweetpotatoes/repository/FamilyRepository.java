package com.juliashouse.sweetpotatoes.repository;

import com.juliashouse.sweetpotatoes.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository
        extends JpaRepository<Family, Long> {

}
