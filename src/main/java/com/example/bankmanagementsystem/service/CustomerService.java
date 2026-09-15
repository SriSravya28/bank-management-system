package com.example.bankmanagementsystem.service;

import com.example.bankmanagementsystem.dto.CustomerRequest;
import com.example.bankmanagementsystem.entity.Customer;
import com.example.bankmanagementsystem.exception.BusinessException;
import com.example.bankmanagementsystem.exception.ResourceNotFoundException;
import com.example.bankmanagementsystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer create(CustomerRequest req) {
        if (customerRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Customer with email already exists: " + req.getEmail());
        }
        Customer customer = Customer.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .build();
        return customerRepository.save(customer);
    }

    public Customer update(String id, CustomerRequest req) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        if (!customer.getEmail().equals(req.getEmail())
                && customerRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email already in use: " + req.getEmail());
        }

        customer.setName(req.getName());
        customer.setEmail(req.getEmail());
        customer.setPhone(req.getPhone());
        return customerRepository.save(customer);
    }

    public void delete(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        customerRepository.delete(customer);
    }

    public Customer get(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }
}