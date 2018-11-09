package com.angusbarnes.bills;

import com.angusbarnes.bills.entity.CredentialSet;
import com.angusbarnes.bills.entity.User;
import com.angusbarnes.bills.repository.CredentialSetRepository;
import com.angusbarnes.bills.repository.UserRepository;
import com.angusbarnes.bills.service.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.SecureRandom;
import java.util.Random;

@SpringBootApplication
@EntityScan(basePackages = {"com.angusbarnes.bills.entity"})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.angusbarnes.bills.repository"})
@ComponentScan({"com.angusbarnes.bills.controller"})
public class BillsApplication {
    
    private static final Logger LOG = LogManager.getRootLogger();
    private static final Random RANDOM = new SecureRandom();
    
    public static void main (String[] args) {
        SpringApplication.run(BillsApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner init (UserRepository uR,
                                   CredentialSetRepository cR) {
        return (args) -> {
            for (int i = 0; i < 5; i++) {
                String username = SecurityService.generateSessionKey(5) + "@a";
                String password = "hello";
                
                int memCost = 8;
                int timCost = 1;
                int parCost = 1;
                byte[] salt = SecurityService.getSalt();
                byte[] hash = SecurityService.hashPassword(
                        password.getBytes(),
                        salt,
                        timCost,
                        memCost,
                        parCost);
                
                User newUser = new User(username);
                CredentialSet newCredentialSet = new CredentialSet(
                        newUser,
                        hash,
                        salt,
                        memCost,
                        timCost,
                        parCost);
                
                uR.save(newUser);
                cR.save(newCredentialSet);
            }
            
            LOG.info("Customers found with findAll():");
            LOG.info("-------------------------------");
            for (User user : uR.findAll()) {
                LOG.info(user.toString());
            }
        };
    }
}
