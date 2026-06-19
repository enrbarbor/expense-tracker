package com.enriquebarragan.expensetracker.repository;

import com.enriquebarragan.expensetracker.model.Transaction;
import com.enriquebarragan.expensetracker.model.TransactionType;
import com.enriquebarragan.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
            "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserAndDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user = :user AND t.type = :type " +
            "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT t.category.name, COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user = :user AND t.type = :type " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY t.category.name " +
            "ORDER BY SUM(t.amount) DESC")
    List<Object[]> sumByCategoryAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}