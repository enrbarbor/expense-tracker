package com.enriquebarragan.expensetracker.controller;

import com.enriquebarragan.expensetracker.dto.CategoryRequest;
import com.enriquebarragan.expensetracker.dto.CategoryResponse;
import com.enriquebarragan.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Management of income and expense categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(categoryService.findAll(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(categoryService.update(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        categoryService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}