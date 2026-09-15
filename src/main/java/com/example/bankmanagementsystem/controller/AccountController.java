package com.example.bankmanagementsystem.controller;

import com.example.bankmanagementsystem.dto.AccountRequest;
import com.example.bankmanagementsystem.dto.AmountRequest;
import com.example.bankmanagementsystem.entity.Account;
import com.example.bankmanagementsystem.entity.Transaction;
import com.example.bankmanagementsystem.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody AccountRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(req));
    }

    @GetMapping("/{id}")
    public Account get(@PathVariable String id) {
        return accountService.get(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> listByCustomer(@PathVariable String customerId) {
        return accountService.listByCustomer(customerId);
    }

    @PatchMapping("/{id}/activate")
    public Account activate(@PathVariable String id) {
        return accountService.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public Account deactivate(@PathVariable String id) {
        return accountService.setActive(id, false);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposit")
    public Transaction deposit(@PathVariable String id, @Valid @RequestBody AmountRequest req) {
        return accountService.deposit(id, req);
    }

    @PostMapping("/{id}/withdraw")
    public Transaction withdraw(@PathVariable String id, @Valid @RequestBody AmountRequest req) {
        return accountService.withdraw(id, req);
    }

    @GetMapping("/{id}/transactions")
    public List<Transaction> transactions(@PathVariable String id) {
        return accountService.listTransactions(id);
    }
}