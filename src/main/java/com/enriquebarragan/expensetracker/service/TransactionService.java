package com.enriquebarragan.expensetracker.service;

import com.enriquebarragan.expensetracker.dto.CategorySummary;
import com.enriquebarragan.expensetracker.dto.SummaryResponse;
import com.enriquebarragan.expensetracker.dto.TransactionRequest;
import com.enriquebarragan.expensetracker.dto.TransactionResponse;
import com.enriquebarragan.expensetracker.model.Category;
import com.enriquebarragan.expensetracker.model.Transaction;
import com.enriquebarragan.expensetracker.model.TransactionType;
import com.enriquebarragan.expensetracker.model.User;
import com.enriquebarragan.expensetracker.repository.CategoryRepository;
import com.enriquebarragan.expensetracker.repository.TransactionRepository;
import com.enriquebarragan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionResponse create(TransactionRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to use this category");
        }

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .type(request.getType())
                .category(category)
                .user(user)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> findAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse findById(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to view this transaction");
        }

        return toResponse(transaction);
    }

    public TransactionResponse update(Long id, TransactionRequest request, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to edit this transaction");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setType(request.getType());
        transaction.setCategory(category);

        return toResponse(transactionRepository.save(transaction));
    }

    public void delete(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to delete this transaction");
        }

        transactionRepository.delete(transaction);
    }

    public SummaryResponse getSummary(Integer month, Integer year, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.INCOME, startDate, endDate);

        BigDecimal totalExpenses = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        List<Object[]> categoryResults = transactionRepository.sumByCategoryAndDateBetween(
                user, TransactionType.EXPENSE, startDate, endDate);

        List<CategorySummary> expensesByCategory = categoryResults.stream()
                .map(row -> {
                    String categoryName = (String) row[0];
                    BigDecimal total = (BigDecimal) row[1];
                    double percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                            ? total.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
                            : 0.0;

                    return CategorySummary.builder()
                            .categoryName(categoryName)
                            .total(total)
                            .percentage(percentage)
                            .build();
                })
                .toList();

        return SummaryResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .expensesByCategory(expensesByCategory)
                .build();
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .type(transaction.getType())
                .categoryName(transaction.getCategory().getName())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}