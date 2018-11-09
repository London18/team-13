package com.angusbarnes.bills.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;


@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String username;
    
    @OneToOne(mappedBy = "user",
              fetch = FetchType.LAZY,
              cascade = CascadeType.ALL)
    private CredentialSet credentialSet;
    
    @OneToMany(mappedBy = "user",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL)
    private List<SessionInstance> sessions;
    
    @OneToMany(mappedBy = "user",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL)
    private List<TransactionGroup> transactions;
    
    protected User () {
    
    }
    
    public User (String username) {
        this.username = username;
    }
    
    public Long getId () {
        return id;
    }
    
    public void setId (Long id) {
        this.id = id;
    }
    
    public String getUsername () {
        return username;
    }
    
    public void setUsername (String username) {
        this.username = username;
    }
    
    public CredentialSet getCredentialSet () {
        return credentialSet;
    }
    
    public void setCredentialSet (CredentialSet credentialSet) {
        this.credentialSet = credentialSet;
    }
    
    public List<SessionInstance> getSessions () {
        return sessions;
    }
    
    public void setSessions (List<SessionInstance> sessions) {
        this.sessions = sessions;
    }
    
    public List<TransactionGroup> getTransactions () {
        return transactions;
    }
    
    public void setTransactions (List<TransactionGroup> transactions) {
        this.transactions = transactions;
    }
    
    @Override
    public String toString () {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
    
}