package com.example.bankmanagementsystem.repository;

import com.example.bankmanagementsystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByCustomerId(String customerId);
    boolean existsByAccountNumber(String accountNumber);
}