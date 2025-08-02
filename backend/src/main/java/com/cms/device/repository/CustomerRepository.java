package com.cms.device.repository;

import com.cms.device.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByCustomerId(String customerId);
    
    boolean existsByCustomerId(String customerId);
    
    List<Customer> findByActive(boolean active);
    
    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:name% OR c.customerId LIKE %:name%")
    List<Customer> findByCustomerNameOrCustomerIdContaining(@Param("name") String name);
    
    @Query("SELECT c FROM Customer c WHERE c.email LIKE %:email%")
    List<Customer> findByEmailContaining(@Param("email") String email);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.active = true")
    long countActiveCustomers();
} 