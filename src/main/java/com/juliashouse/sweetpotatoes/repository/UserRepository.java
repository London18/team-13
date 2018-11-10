package com.juliashouse.sweetpotatoes.repository;

import com.juliashouse.sweetpotatoes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername (String username);
}