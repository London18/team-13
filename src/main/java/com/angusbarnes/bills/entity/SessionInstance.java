package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "sessionInstance")
@EntityListeners(AuditingEntityListener.class)
public class SessionInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
    
    private String sessionKey;
    
    private Date expiryDate;
    
    protected SessionInstance () {
    
    }
    
    public SessionInstance (User user, String sessionKey, Date expiryDate) {
        this.user = user;
        this.sessionKey = sessionKey;
        this.expiryDate = expiryDate;
    }
    
    @Override
    public String toString () {
        return "SessionInstance{" +
                "user=" + user +
                ", sessionKey='" + sessionKey + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
    
    public Long getId () {
        return id;
    }
    
    public void setId (Long id) {
        this.id = id;
    }
    
    public User getUser () {
        return user;
    }
    
    public void setUser (User user) {
        this.user = user;
    }
    
    public String getSessionKey () {
        return sessionKey;
    }
    
    public void setSessionKey (String sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public Date getExpiryDate () {
        return expiryDate;
    }
    
    public void setExpiryDate (Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
