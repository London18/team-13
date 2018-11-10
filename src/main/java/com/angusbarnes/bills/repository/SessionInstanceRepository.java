package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.Carer;
import com.angusbarnes.bills.entity.SessionInstance;
import com.angusbarnes.bills.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SessionInstanceRepository
        extends JpaRepository<SessionInstance, Long> {
    
    List<SessionInstance> findBySessionKey (String sessionKey);
    
    List<SessionInstance> findBySessionKeyAndExpiryDateGreaterThan (
            String sessionKey,
            Date expiryDate);
    
    List<SessionInstance> findByUserAndExpiryDateGreaterThan (
            User user,
            Date expiryDate);
    
}