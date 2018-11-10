package com.juliashouse.sweetpotatoes.repository;

import com.juliashouse.sweetpotatoes.entity.VisitUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitUpdateRepository
        extends JpaRepository<VisitUpdate, Long> {

}
