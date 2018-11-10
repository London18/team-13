package com.juliashouse.sweetpotatoes.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Entity
@Table(name = "credentialSet")
@EntityListeners(AuditingEntityListener.class)
public class CredentialSet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    private byte[] passwordHash;
    private byte[] passwordSalt;
    
    private int memoryCost;
    private int timeCost;
    private int parallelism;
    
    protected CredentialSet () {
    
    }
    
    public CredentialSet (User user,
                          byte[] passwordHash,
                          byte[] passwordSalt,
                          int memoryCost, int timeCost, int parallelism) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.memoryCost = memoryCost;
        this.timeCost = timeCost;
        this.parallelism = parallelism;
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
    
    public byte[] getPasswordHash () {
        return passwordHash;
    }
    
    public void setPasswordHash (byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public byte[] getPasswordSalt () {
        return passwordSalt;
    }
    
    public void setPasswordSalt (byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
    
    public int getMemoryCost () {
        return memoryCost;
    }
    
    public void setMemoryCost (int memoryCost) {
        this.memoryCost = memoryCost;
    }
    
    public int getTimeCost () {
        return timeCost;
    }
    
    public void setTimeCost (int timeCost) {
        this.timeCost = timeCost;
    }
    
    public int getParallelism () {
        return parallelism;
    }
    
    public void setParallelism (int parallelism) {
        this.parallelism = parallelism;
    }
}
