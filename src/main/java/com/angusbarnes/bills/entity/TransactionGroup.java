package com.angusbarnes.bills.entity;

import com.angusbarnes.bills.service.Base64Service;
import com.angusbarnes.bills.service.DateService;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;

@Entity
@Table(name = "transactionGroup")
@EntityListeners(AuditingEntityListener.class)
public class TransactionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    
    private Date startDate;
    private Date finishDate;
    
    private byte[] rawData;
    private byte[] iv;
    
    @OneToOne(mappedBy = "transactionGroup",
              fetch = FetchType.LAZY,
              cascade = CascadeType.ALL)
    private DataEncryptionKey dataEncryptionKey;
    
    protected TransactionGroup () {
    
    }
    
    @Override
    public String toString () {
        return "TransactionGroup{" +
                "id=" + id +
                ", user=" + user +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                ", rawData=" + Base64Service.toBase64(rawData) +
                ", iv=" + Base64Service.toBase64(iv) +
                ", dataEncryptionKey=" + dataEncryptionKey +
                '}';
    }
    
    public TransactionGroup (User user,
                             Date startDate,
                             Date finishDate,
                             byte[] rawData,
                             byte[] iv) {
        this.user = user;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.rawData = rawData;
        this.iv = iv;
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
    
    public Date getStartDate () {
        return startDate;
    }
    
    public void setStartDate (Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getFinishDate () {
        return finishDate;
    }
    
    public void setFinishDate (Date finishDate) {
        this.finishDate = finishDate;
    }
    
    public byte[] getRawData () {
        return rawData;
    }
    
    public void setRawData (byte[] rawData) {
        this.rawData = rawData;
    }
    
    public byte[] getIv () {
        return iv;
    }
    
    public void setIv (byte[] iv) {
        this.iv = iv;
    }
    
    public DataEncryptionKey getDataEncryptionKey () {
        return dataEncryptionKey;
    }
    
    public void setDataEncryptionKey (DataEncryptionKey dataEncryptionKey) {
        this.dataEncryptionKey = dataEncryptionKey;
    }
}
