package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.Family;
import com.angusbarnes.bills.entity.ScheduleCarer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleCarerRepository
        extends JpaRepository<ScheduleCarer, Long> {

    Optional<ScheduleCarer> findById(long SCID);
}
