package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.ScheduleCarer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleCarerRepository
        extends JpaRepository<ScheduleCarer, Long> {

}
