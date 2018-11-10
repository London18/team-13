package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "scheduleCarer")
@EntityListeners(AuditingEntityListener.class)
public class ScheduleCarer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleEvent")
    ScheduleEvent scheduleEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carer")
    Carer carer;

    @OneToMany(mappedBy = "scheduleCarer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    List<VisitUpdate> visitUpdates;
}
