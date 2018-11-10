package com.juliashouse.sweetpotatoes.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
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
              cascade = CascadeType.ALL,
              optional = false)
    private CredentialSet credentialSet;

    @OneToOne(mappedBy = "user",
              fetch = FetchType.LAZY,
              cascade = CascadeType.ALL)
    private Carer carer;
    
    @OneToMany(mappedBy = "user",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL)
    private List<SessionInstance> sessions;



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

    public Carer getCarer() {
        return carer;
    }

    public void setCarer(Carer carer) {
        this.carer = carer;
    }

    @Override
    public String toString () {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
    
}