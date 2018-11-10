package com.angusbarnes.bills;

import com.angusbarnes.bills.entity.*;
import com.angusbarnes.bills.repository.*;
import com.angusbarnes.bills.service.DateService;
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
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Date;


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
                                   CredentialSetRepository cR,
                                   CarerRepository carerRepository,
                                   FamilyRepository familyRepository,
                                   ScheduleEventRepository scheduleEventRepository,
                                   VisitUpdateRepository visitUpdateRepository,
                                   ScheduleCarerRepository scheduleCarerRepository) {
        return (args) -> {
            for (int i = 0; i < 15; i++) {
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
                Carer newCarer = new Carer(newUser, "123 nowhere st", "07000000000", "bob", "smith");
                carerRepository.save(newCarer);
                Family newFamily = new Family (SecurityService.generateSessionKey(5), "23 New Street");
                familyRepository.save(newFamily);



                Date date = new Date();

                ScheduleEvent newScheduleEvent = new ScheduleEvent(date, DateService.later(date, 3, 20), newFamily);
                scheduleEventRepository.save(newScheduleEvent);



                ScheduleCarer newScheduleCarer = new ScheduleCarer(newScheduleEvent, newCarer);
                scheduleCarerRepository.save(newScheduleCarer);

                VisitUpdate newVisitUpdate = new VisitUpdate(newScheduleCarer, "arrived", date, SecurityService.generateSessionKey(5));
                visitUpdateRepository.save(newVisitUpdate);

            }







            LOG.info("Customers found with findAll():");
            LOG.info("-------------------------------");
            for (User user : uR.findAll()) {
                LOG.info(user.toString());
            }

            List<User> allUsers = uR.findAll();
            for (User user : allUsers) {
                LOG.info(user.getCarer());
            }

            List<Family> families = familyRepository.findAll();
            for (Family family : families) {
                LOG.info(family);
            }


        };
    }
}
