package com.enriquebarragan.expensetracker.repository;

import com.enriquebarragan.expensetracker.model.Category;
import com.enriquebarragan.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
}