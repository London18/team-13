package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.VisitUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitUpdateRepository
        extends JpaRepository<VisitUpdate, Long> {

}
