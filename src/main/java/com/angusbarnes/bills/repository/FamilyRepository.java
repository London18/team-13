package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyRepository
        extends JpaRepository<Family, Long> {

}
