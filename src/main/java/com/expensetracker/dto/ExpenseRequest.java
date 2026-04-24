package com.expensetracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Data
public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;
    @NotNull(message = "Date is required")
    private LocalDate date;
}
