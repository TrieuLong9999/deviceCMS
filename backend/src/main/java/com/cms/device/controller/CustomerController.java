package com.cms.device.controller;

import com.cms.device.dto.CustomerDto;
import com.cms.device.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with optional key pair generation")
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        try {
            CustomerDto createdCustomer = customerService.createCustomer(customerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Updates an existing customer")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable String customerId, 
                                                   @Valid @RequestBody CustomerDto customerDto) {
        try {
            CustomerDto updatedCustomer = customerService.updateCustomer(customerId, customerDto);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their ID")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable String customerId) {
        try {
            CustomerDto customer = customerService.getCustomerById(customerId);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves all customers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active customers", description = "Retrieves all active customers")
    public ResponseEntity<List<CustomerDto>> getActiveCustomers() {
        List<CustomerDto> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Searches customers by name or ID")
    public ResponseEntity<List<CustomerDto>> searchCustomers(@RequestParam String term) {
        List<CustomerDto> customers = customerService.searchCustomers(term);
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/search/email")
    @Operation(summary = "Search customers by email", description = "Searches customers by email")
    public ResponseEntity<List<CustomerDto>> searchCustomersByEmail(@RequestParam String email) {
        List<CustomerDto> customers = customerService.searchCustomersByEmail(email);
        return ResponseEntity.ok(customers);
    }
    
    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer", description = "Deletes a customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{customerId}/deactivate")
    @Operation(summary = "Deactivate customer", description = "Deactivates a customer")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable String customerId) {
        try {
            customerService.deactivateCustomer(customerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count/active")
    @Operation(summary = "Get active customer count", description = "Returns the count of active customers")
    public ResponseEntity<Long> getActiveCustomerCount() {
        long count = customerService.getActiveCustomerCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{customerId}/exists")
    @Operation(summary = "Check customer exists", description = "Checks if a customer exists")
    public ResponseEntity<Boolean> customerExists(@PathVariable String customerId) {
        boolean exists = customerService.customerExists(customerId);
        return ResponseEntity.ok(exists);
    }
    
    @PostMapping("/{customerId}/regenerate-keys")
    @Operation(summary = "Regenerate customer keys", description = "Regenerates the key pair for a customer")
    public ResponseEntity<CustomerDto> regenerateKeys(@PathVariable String customerId) {
        try {
            CustomerDto updatedCustomer = customerService.regenerateKeys(customerId);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 