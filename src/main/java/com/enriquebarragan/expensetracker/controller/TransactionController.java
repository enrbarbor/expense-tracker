package com.enriquebarragan.expensetracker.controller;

import com.enriquebarragan.expensetracker.dto.TransactionRequest;
import com.enriquebarragan.expensetracker.dto.TransactionResponse;
import com.enriquebarragan.expensetracker.service.TransactionService;
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
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Management of financial transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.create(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.findAll(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.findById(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(transactionService.update(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        transactionService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}