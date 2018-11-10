package com.juliashouse.sweetpotatoes.entity;

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

    private Date start;
    private Date end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family")
    private Family family;

    protected ScheduleEvent() {
    }

    public ScheduleEvent(Date start, Date end, Family family) {
        this.start = start;
        this.end = end;
        this.family = family;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

//    @Override
//    public String toString() {
////        return "hmmm";
////        return "ScheduleEvent{" +
////                "start=" + start +
////                ", end=" + end +
////                ", family=" + family +
////                '}';
//    }
}
