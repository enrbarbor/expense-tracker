package com.enriquebarragan.expensetracker.dto;

import com.enriquebarragan.expensetracker.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Type is required")
    private TransactionType type;
}