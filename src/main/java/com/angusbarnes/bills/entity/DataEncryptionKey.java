package com.angusbarnes.bills.entity;

import com.angusbarnes.bills.service.Base64Service;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
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
@Table(name = "dataEncryptionKey")
@EntityListeners(AuditingEntityListener.class)
public class DataEncryptionKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private byte[] encryptedKey;
    private byte[] iv;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionGroup")
    private TransactionGroup transactionGroup;
    
    @OneToOne(mappedBy = "dataEncryptionKey",
              fetch = FetchType.LAZY,
              cascade = CascadeType.ALL)
    private KeyEncryptionKeyData keyEncryptionKeyData;
    
    protected DataEncryptionKey () {
    
    }
    
    public DataEncryptionKey (byte[] encryptedKey,
                              byte[] iv,
                              TransactionGroup transactionGroup) {
        this.encryptedKey = encryptedKey;
        this.iv = iv;
        this.transactionGroup = transactionGroup;
    }
    
    public Long getId () {
        return id;
    }
    
    public void setId (Long id) {
        this.id = id;
    }
    
    @Override
    public String toString () {
        return "DataEncryptionKey{" +
                "id=" + id +
                ", encryptedKey=" + Base64Service.toBase64(encryptedKey) +
                ", iv=" + Base64Service.toBase64(iv) +
                ", keyEncryptionKeyData=" + keyEncryptionKeyData +
                '}';
    }
    
    public byte[] getEncryptedKey () {
        return encryptedKey;
    }
    
    public void setEncryptedKey (byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }
    
    public byte[] getIv () {
        return iv;
    }
    
    public void setIv (byte[] iv) {
        this.iv = iv;
    }
    
    public TransactionGroup getTransactionGroup () {
        return transactionGroup;
    }
    
    public void setTransactionGroup (TransactionGroup transactionGroup) {
        this.transactionGroup = transactionGroup;
    }
    
    public KeyEncryptionKeyData getKeyEncryptionKeyData () {
        return keyEncryptionKeyData;
    }
    
    public void setKeyEncryptionKeyData (KeyEncryptionKeyData keyEncryptionKeyData) {
        this.keyEncryptionKeyData = keyEncryptionKeyData;
    }
}
