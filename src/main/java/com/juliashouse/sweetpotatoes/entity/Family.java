package com.juliashouse.sweetpotatoes.entity;

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

    private String name;
    private String address;

    @OneToMany(mappedBy = "family",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ScheduleEvent> scheduleEvents;

    protected Family() {
    }

    public Family(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ScheduleEvent> getScheduleEvents() {
        return scheduleEvents;
    }

    public void setScheduleEvents(List<ScheduleEvent> scheduleEvents) {
        this.scheduleEvents = scheduleEvents;
    }

    @Override
    public String toString() {
//        return "";
        return "Family{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
