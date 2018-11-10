package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "family")
@EntityListeners(AuditingEntityListener.class)
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String name;
    String address;

    @OneToMany(mappedBy = "family",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    List<ScheduleEvent> scheduleEvents;
}
