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
    private ScheduleCarer scheduleCarer;

    private String action;

    private Date time;

    private String comments;

    protected VisitUpdate() {
    }

    public VisitUpdate(ScheduleCarer scheduleCarer, String action, Date time, String comments) {
        this.scheduleCarer = scheduleCarer;
        this.action = action;
        this.time = time;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleCarer getScheduleCarer() {
        return scheduleCarer;
    }

    public void setScheduleCarer(ScheduleCarer scheduleCarer) {
        this.scheduleCarer = scheduleCarer;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "VisitUpdate{" +
                "action='" + action + '\'' +
                ", time=" + time +
                ", comments='" + comments + '\'' +
                '}';
    }
}
