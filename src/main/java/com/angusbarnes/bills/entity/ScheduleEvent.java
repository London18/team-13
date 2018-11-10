package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "scheduleEvent")
@EntityListeners(AuditingEntityListener.class)
public class ScheduleEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Date start;
    Date end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family")
    Family family;
}
