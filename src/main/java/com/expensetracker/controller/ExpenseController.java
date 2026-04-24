package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseListResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseRequest request, @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Expense expense = expenseService.createExpense(request.getAmount(), request.getCategory(), request.getDescription(), request.getDate(), idempotencyKey);
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<ExpenseListResponse> getExpenses(@RequestParam(required = false) String category, @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(expenseService.getExpenses(category, sort));
    }
}