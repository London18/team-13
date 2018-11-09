package com.angusbarnes.bills.repository;

import com.angusbarnes.bills.entity.KeyEncryptionKeyData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyEncryptionKeyDataRepository
        extends JpaRepository<KeyEncryptionKeyData, Long> {
}
