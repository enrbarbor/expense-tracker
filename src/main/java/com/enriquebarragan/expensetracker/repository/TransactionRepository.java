package com.enriquebarragan.expensetracker.repository;

import com.enriquebarragan.expensetracker.model.Transaction;
import com.enriquebarragan.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}