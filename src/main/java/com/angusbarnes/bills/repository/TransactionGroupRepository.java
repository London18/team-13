package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.TransactionGroup;
import com.angusbarnes.bills.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionGroupRepository
        extends JpaRepository<TransactionGroup, Long> {
    List<TransactionGroup> findByUserAndStartDate (User user, Date startDate);
    
    List<TransactionGroup> findByUser (User user);
}
