package com.juliashouse.sweetpotatoes;

import com.juliashouse.sweetpotatoes.entity.*;
import com.juliashouse.sweetpotatoes.repository.*;
import com.juliashouse.sweetpotatoes.service.DateService;
import com.juliashouse.sweetpotatoes.service.SecurityService;
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
import org.springframework.scheduling.annotation.EnableScheduling;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Date;
import java.util.Set;


@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"com.juliashouse.sweetpotatoes.entity"})
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.juliashouse.sweetpotatoes.repository"})
@ComponentScan({"com.juliashouse.sweetpotatoes.controller"})
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
        String[] usernames = {"rob@a", "bob@b", "cob@c", "dob@d"};
        String[] passwords = {"1234", "1234", "1234", "1234"};
        String[] addresses = {"123 nowhere st", "456 nope ln", "142 wat", "8 nooo"};
        String[] firstNames = {"rob", "bob", "cob", "dob"};
        String[] lastNames = {"smith", "dith", "miff", "tiff"};
        String[] familyNames = {"wilson", "wilsooon", "walson", "vumps"};
        String[] addresses2 = {"addr1", "addr2", "addr3", "addr4"};
        int[] dateOffsets = {10, 100, 1000, -10};
        String[] comments = {"10 after", "100 after", "1000 after", "10 before"};

        return (args) -> {
            for (int i = 0; i < usernames.length; i++) {
                int memCost = 8;
                int timCost = 1;
                int parCost = 1;
                byte[] salt = SecurityService.getSalt();
                byte[] hash = SecurityService.hashPassword(
                        passwords[i].getBytes(),
                        salt,
                        timCost,
                        memCost,
                        parCost);

                User newUser = new User(usernames[i]);
                CredentialSet newCredentialSet = new CredentialSet(
                        newUser,
                        hash,
                        salt,
                        memCost,
                        timCost,
                        parCost);

                uR.save(newUser);
                cR.save(newCredentialSet);

                Carer newCarer = new Carer(newUser, addresses[i], "07000000000", firstNames[i], lastNames[i]);
                carerRepository.save(newCarer);
                Family newFamily = new Family(familyNames[i], addresses2[i]);
                familyRepository.save(newFamily);

                Date newDate = new Date();
                Date futureDate = DateService.later(newDate, 0, 5);
                Date futureDate2 = DateService.later(newDate, 0, dateOffsets[i]);
                ScheduleEvent newScheduleEvent = new ScheduleEvent(newDate, futureDate, newFamily);
                ScheduleCarer newScheduleCarer = new ScheduleCarer(newScheduleEvent, newCarer);
                VisitUpdate newVisitUpdate = new VisitUpdate(newScheduleCarer, "arrived", futureDate2, comments[i]);

                scheduleEventRepository.save(newScheduleEvent);
                scheduleCarerRepository.save(newScheduleCarer);
                visitUpdateRepository.save(newVisitUpdate);
            }

            LOG.info("Customers found with findAll():");
            LOG.info("-------------------------------");
            for (User user : uR.findAll()) {
                LOG.info(user.toString());
            }

//            List<User> allUsers = uR.findAll();
//            for (User user : allUsers) {
//                LOG.info(user.getCarer());
//            }

//            List<Family> families = familyRepository.findAll();
//            for (Family family : families) {
//                LOG.info(family);
//            }

            scheduleEventRepository.findAll().stream().map(ScheduleEvent::toString).forEach(System.out::println);
            visitUpdateRepository.findAll().stream().map(VisitUpdate::toString).forEach(System.out::println);
        };
    }
}
