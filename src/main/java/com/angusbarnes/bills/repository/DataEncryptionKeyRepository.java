package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.DataEncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataEncryptionKeyRepository
        extends JpaRepository<DataEncryptionKey, Long> {
}
