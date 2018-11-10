package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "carer")
@EntityListeners(AuditingEntityListener.class)
public class Carer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(mappedBy = "carer",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ScheduleCarer> scheduleCarers;

    private String address;
    private String phone;
    private String firstName;
    private String lastName;

    protected Carer() {
    }

    public Carer(User user, String address, String phone, String firstName, String lastName) {
        this.user = user;
        this.address = address;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ScheduleCarer> getScheduleCarers() {
        return scheduleCarers;
    }

    public void setScheduleCarers(List<ScheduleCarer> scheduleCarers) {
        this.scheduleCarers = scheduleCarers;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Carer{" +
                "user=" + user +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
