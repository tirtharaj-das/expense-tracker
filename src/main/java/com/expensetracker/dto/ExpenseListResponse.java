package com.expensetracker.dto;

import com.expensetracker.model.Expense;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ExpenseListResponse {

    private List<Expense> expenses;
    private BigDecimal total;
}