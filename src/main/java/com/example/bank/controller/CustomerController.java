package com.example.bank.controller;

import com.example.bank.dto.CustomerRequest;
import com.example.bank.dto.CustomerResponse;
import com.example.bank.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    // Still in memory: getAll, update and delete use this until we migrate them.
    private final Map<Long, CustomerResponse> customers = new ConcurrentHashMap<>();

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        long id = customerRepository.save(request.name(), request.email());
        CustomerResponse customer = new CustomerResponse(id, request.name(), request.email());

        URI location = URI.create("/api/customers/" + id);
        return ResponseEntity.created(location).body(customer);
    }

//    @GetMapping
//    public List<CustomerResponse> getAllCustomers() {
//        return new ArrayList<>(customers.values());
//    }
@GetMapping
public List<CustomerResponse> getAllCustomers() {
    return customerRepository.findAll();
}

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequest request) {

        boolean updated = customerRepository.update(id, request.name(), request.email());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new CustomerResponse(id, request.name(), request.email()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerRepository.deleteById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}