package com.example.bankmanagementsystem.service;

import com.example.bankmanagementsystem.dto.AccountRequest;
import com.example.bankmanagementsystem.dto.AmountRequest;
import com.example.bankmanagementsystem.entity.Account;
import com.example.bankmanagementsystem.entity.Customer;
import com.example.bankmanagementsystem.entity.Transaction;
import com.example.bankmanagementsystem.entity.TransactionType;
import com.example.bankmanagementsystem.exception.BusinessException;
import com.example.bankmanagementsystem.exception.ResourceNotFoundException;
import com.example.bankmanagementsystem.repository.AccountRepository;
import com.example.bankmanagementsystem.repository.CustomerRepository;
import com.example.bankmanagementsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public Account create(AccountRequest req) {
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new BusinessException(
                        "Cannot create account: customer does not exist with id " + req.getCustomerId()));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .accountType(req.getAccountType())
                .balance(BigDecimal.ZERO)
                .isActive(true)
                .customer(customer)
                .build();

        return accountRepository.save(account);
    }

    public Account get(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    public List<Account> listByCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }
        return accountRepository.findByCustomerId(customerId);
    }

    public Account setActive(String id, boolean active) {
        Account account = get(id);
        account.setIsActive(active);
        return accountRepository.save(account);
    }

    public void delete(String id) {
        Account account = get(id);
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Cannot delete account with non-zero balance");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public Transaction deposit(String accountId, AmountRequest req) {
        Account account = get(accountId);
        if (!account.getIsActive()) {
            throw new BusinessException("Cannot deposit: account is inactive");
        }
        account.setBalance(account.getBalance().add(req.getAmount()));
        accountRepository.save(account);
        return recordTransaction(account, TransactionType.DEPOSIT, req.getAmount());
    }

    @Transactional
    public Transaction withdraw(String accountId, AmountRequest req) {
        Account account = get(accountId);
        if (!account.getIsActive()) {
            throw new BusinessException("Cannot withdraw: account is inactive");
        }
        if (account.getBalance().compareTo(req.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance. Available: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(req.getAmount()));
        accountRepository.save(account);
        return recordTransaction(account, TransactionType.WITHDRAWAL, req.getAmount());
    }

    public List<Transaction> listTransactions(String accountId) {
        get(accountId); // verifies account exists
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    private Transaction recordTransaction(Account account, TransactionType type, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .build();
        return transactionRepository.save(transaction);
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}