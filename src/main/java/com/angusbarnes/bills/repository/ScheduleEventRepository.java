package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.Family;
import com.angusbarnes.bills.entity.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleEventRepository
        extends JpaRepository<ScheduleEvent, Long> {

}
