package com.enriquebarragan.expensetracker.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("enrique").build();
        testCategory = Category.builder()
                .id(1L)
                .name("Groceries")
                .type(TransactionType.EXPENSE)
                .user(testUser)
                .build();
    }

    @Test
    void create_shouldCreateTransactionCorrectly() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(50))
                .description("Supermarket")
                .date(LocalDate.of(2026, 6, 14))
                .type(TransactionType.EXPENSE)
                .categoryId(1L)
                .build();

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50))
                .description("Supermarket")
                .date(LocalDate.of(2026, 6, 14))
                .type(TransactionType.EXPENSE)
                .category(testCategory)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("enrique")).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse response = transactionService.create(request, "enrique");

        assertThat(response.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(response.getCategoryName()).isEqualTo("Groceries");
    }

    @Test
    void getSummary_shouldCalculateBalanceCorrectly() {
        when(userRepository.findByUsername("enrique")).thenReturn(Optional.of(testUser));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                eq(testUser), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(BigDecimal.valueOf(2000));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                eq(testUser), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(BigDecimal.valueOf(450));

        when(transactionRepository.sumByCategoryAndDateBetween(
                eq(testUser), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(List.of(
                        new Object[]{"Groceries", BigDecimal.valueOf(300)},
                        new Object[]{"Transport", BigDecimal.valueOf(150)}
                ));

        SummaryResponse summary = transactionService.getSummary(6, 2026, "enrique");

        assertThat(summary.getTotalIncome()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        assertThat(summary.getTotalExpenses()).isEqualByComparingTo(BigDecimal.valueOf(450));
        assertThat(summary.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1550));
        assertThat(summary.getExpensesByCategory()).hasSize(2);
        assertThat(summary.getExpensesByCategory().getFirst().getPercentage()).isEqualTo(66.67);
    }
}