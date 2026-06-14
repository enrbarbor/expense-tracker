package com.enriquebarragan.expensetracker.dto;

import com.enriquebarragan.expensetracker.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @NotNull(message = "Category is required")
    private Long categoryId;
}