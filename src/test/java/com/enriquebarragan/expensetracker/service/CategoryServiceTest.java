package com.enriquebarragan.expensetracker.service;

import com.enriquebarragan.expensetracker.dto.CategoryRequest;
import com.enriquebarragan.expensetracker.dto.CategoryResponse;
import com.enriquebarragan.expensetracker.model.Category;
import com.enriquebarragan.expensetracker.model.TransactionType;
import com.enriquebarragan.expensetracker.model.User;
import com.enriquebarragan.expensetracker.repository.CategoryRepository;
import com.enriquebarragan.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("enrique")
                .password("encoded_password")
                .build();

        testCategory = Category.builder()
                .id(1L)
                .name("Groceries")
                .type(TransactionType.EXPENSE)
                .user(testUser)
                .build();
    }

    @Test
    void create_shouldSaveAndReturnCategory() {
        CategoryRequest request = new CategoryRequest("Groceries", TransactionType.EXPENSE);

        when(userRepository.findByUsername("enrique")).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        CategoryResponse response = categoryService.create(request, "enrique");

        assertThat(response.getName()).isEqualTo("Groceries");
        assertThat(response.getType()).isEqualTo(TransactionType.EXPENSE);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void create_shouldThrowExceptionIfUserDoesNotExist() {
        CategoryRequest request = new CategoryRequest("Groceries", TransactionType.EXPENSE);

        when(userRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> categoryService.create(request, "Unknown"));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_shouldThrowExceptionIfNotOwner() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        assertThrows(RuntimeException.class,
                () -> categoryService.delete(1L, "another-user"));

        verify(categoryRepository, never()).delete(any());
    }
}