package com.cms.device.service;

import com.cms.device.dto.CustomerDto;
import com.cms.device.entity.Customer;
import com.cms.device.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerRepository.existsByCustomerId(customerDto.getCustomerId())) {
            throw new RuntimeException("Customer with ID " + customerDto.getCustomerId() + " already exists");
        }
        
        Customer customer = new Customer();
        customer.setCustomerId(customerDto.getCustomerId());
        customer.setCustomerName(customerDto.getCustomerName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhone(customerDto.getPhone());
        customer.setActive(customerDto.isActive());
        
        // Generate key pair if not provided
        if (customerDto.getPublicKey() == null || customerDto.getPrivateKey() == null) {
            KeyPair keyPair = generateKeyPair();
            customer.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            customer.setPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        } else {
            customer.setPublicKey(customerDto.getPublicKey());
            customer.setPrivateKey(customerDto.getPrivateKey());
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDto(savedCustomer);
    }
    
    public CustomerDto updateCustomer(String customerId, CustomerDto customerDto) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        customer.setCustomerName(customerDto.getCustomerName());
        customer.setEmail(customerDto.getEmail());
        customer.setPhone(customerDto.getPhone());
        customer.setActive(customerDto.isActive());
        
        if (customerDto.getPublicKey() != null) {
            customer.setPublicKey(customerDto.getPublicKey());
        }
        if (customerDto.getPrivateKey() != null) {
            customer.setPrivateKey(customerDto.getPrivateKey());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDto(updatedCustomer);
    }
    
    public CustomerDto getCustomerById(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        return convertToDto(customer);
    }
    
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDto> getActiveCustomers() {
        return customerRepository.findByActive(true).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDto> searchCustomers(String searchTerm) {
        return customerRepository.findByCustomerNameOrCustomerIdContaining(searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDto> searchCustomersByEmail(String email) {
        return customerRepository.findByEmailContaining(email).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public void deleteCustomer(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        customerRepository.delete(customer);
    }
    
    public void deactivateCustomer(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        customer.setActive(false);
        customerRepository.save(customer);
    }
    
    public long getActiveCustomerCount() {
        return customerRepository.countActiveCustomers();
    }
    
    public boolean customerExists(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }
    
    public CustomerDto regenerateKeys(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        KeyPair keyPair = generateKeyPair();
        customer.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        customer.setPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDto(updatedCustomer);
    }
    
    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Error generating key pair", e);
        }
    }
    
    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setPublicKey(customer.getPublicKey());
        dto.setPrivateKey(customer.getPrivateKey());
        dto.setActive(customer.isActive());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }
} 