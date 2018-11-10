package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "visitUpdate")
@EntityListeners(AuditingEntityListener.class)
public class VisitUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleCarer")
    ScheduleCarer scheduleCarer;

    String action;

    Date time;

    String comments;
}
