package com.juliashouse.sweetpotatoes.repository;

import com.juliashouse.sweetpotatoes.entity.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleEventRepository
        extends JpaRepository<ScheduleEvent, Long> {

}
