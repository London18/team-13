package com.juliashouse.sweetpotatoes.repository;

import com.juliashouse.sweetpotatoes.entity.Carer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarerRepository
        extends JpaRepository<Carer, Long> {
//    List<Carer> findAll();
}
