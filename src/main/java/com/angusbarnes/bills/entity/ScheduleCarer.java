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
    private ScheduleEvent scheduleEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carer")
    private Carer carer;

    @OneToMany(mappedBy = "scheduleCarer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<VisitUpdate> visitUpdates;

    protected ScheduleCarer() {
    }

    public ScheduleCarer(ScheduleEvent scheduleEvent, Carer carer, List<VisitUpdate> visitUpdates) {
        this.scheduleEvent = scheduleEvent;
        this.carer = carer;
        this.visitUpdates = visitUpdates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleEvent getScheduleEvent() {
        return scheduleEvent;
    }

    public void setScheduleEvent(ScheduleEvent scheduleEvent) {
        this.scheduleEvent = scheduleEvent;
    }

    public Carer getCarer() {
        return carer;
    }

    public void setCarer(Carer carer) {
        this.carer = carer;
    }

    public List<VisitUpdate> getVisitUpdates() {
        return visitUpdates;
    }

    public void setVisitUpdates(List<VisitUpdate> visitUpdates) {
        this.visitUpdates = visitUpdates;
    }

    @Override
    public String toString() {
        return "ScheduleCarer{" +
                "scheduleEvent=" + scheduleEvent +
                ", carer=" + carer +
                '}';
    }
}
