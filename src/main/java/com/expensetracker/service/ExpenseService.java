package com.expensetracker.service;

import com.expensetracker.dto.ExpenseListResponse;
import com.expensetracker.exceptions.DuplicateRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public Expense createExpense(BigDecimal amount, String category, String description, LocalDate date, String idempotencyKey) {
        log.info("Creating expense: category={}, amount={}", category, amount);
        Optional<Expense> existing = expenseRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Duplicate request detected for key={}", idempotencyKey);
            return existing.get();
        }
        Expense expense = Expense.builder().amount(amount).category(category).description(description).date(date).idempotencyKey(idempotencyKey).build();
        try {
            return expenseRepository.save(expense);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Race condition detected for idempotencyKey={}", idempotencyKey);
            return expenseRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(() -> new DuplicateRequestException("Duplicate request detected"));
        }
    }

    public ExpenseListResponse getExpenses(String category, String sort) {
        log.info("Fetching expenses: category={}, sort={}", category, sort);
        List<Expense> expenses;
        if (category != null && !category.isBlank()) {
            expenses = expenseRepository.findByCategory(category);
        } else {
            expenses = expenseRepository.findAll();
        }
        if ("date_desc".equals(sort)) {
            expenses.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        }
        log.info("Fetched {} expenses", expenses.size());
        BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Total calculated = {}", total);
        return new ExpenseListResponse(expenses, total);
    }
}
