package com.angusbarnes.bills.entity;

import com.angusbarnes.bills.service.Base64Service;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Arrays;

@Entity
@Table(name = "keyEncryptionKeyData")
@EntityListeners(AuditingEntityListener.class)
public class KeyEncryptionKeyData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private byte[] salt;
    
    private int memoryCost;
    private int timeCost;
    private int parallelism;
    
    @Override
    public String toString () {
        return "KeyEncryptionKeyData{" +
                "id=" + id +
                ", salt=" + Base64Service.toBase64(salt) +
                ", memoryCost=" + memoryCost +
                ", timeCost=" + timeCost +
                ", parallelism=" + parallelism +
                '}';
    }
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataEncryptionKey")
    private DataEncryptionKey dataEncryptionKey;
    
    protected KeyEncryptionKeyData () {
    
    }
    
    public KeyEncryptionKeyData (byte[] salt,
                                 int memoryCost,
                                 int timeCost,
                                 int parallelism,
                                 DataEncryptionKey dataEncryptionKey) {
        this.salt = salt;
        this.memoryCost = memoryCost;
        this.timeCost = timeCost;
        this.parallelism = parallelism;
        this.dataEncryptionKey = dataEncryptionKey;
    }
    
    public Long getId () {
        return id;
    }
    
    public void setId (Long id) {
        this.id = id;
    }
    
    public byte[] getSalt () {
        return salt;
    }
    
    public void setSalt (byte[] salt) {
        this.salt = salt;
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
    
    public DataEncryptionKey getDataEncryptionKey () {
        return dataEncryptionKey;
    }
    
    public void setDataEncryptionKey (DataEncryptionKey dataEncryptionKey) {
        this.dataEncryptionKey = dataEncryptionKey;
    }
}
