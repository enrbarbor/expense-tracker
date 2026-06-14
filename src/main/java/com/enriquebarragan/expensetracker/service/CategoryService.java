package com.enriquebarragan.expensetracker.service;

import com.enriquebarragan.expensetracker.dto.CategoryRequest;
import com.enriquebarragan.expensetracker.dto.CategoryResponse;
import com.enriquebarragan.expensetracker.model.Category;
import com.enriquebarragan.expensetracker.model.User;
import com.enriquebarragan.expensetracker.repository.CategoryRepository;
import com.enriquebarragan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryResponse create(CategoryRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public List<CategoryResponse> findAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return categoryRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse update(Long id, CategoryRequest request, String username) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to edit this category");
        }

        category.setName(request.getName());
        category.setType(request.getType());

        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id, String username) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You do not have permission to delete this category");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}