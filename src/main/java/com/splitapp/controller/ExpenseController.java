package com.splitapp.controller;

import com.splitapp.model.Expense;
import com.splitapp.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @GetMapping("/expenses")
    public ResponseEntity<?> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(createResponse(true, expenses, "Expenses retrieved successfully"));
    }
    
    @PostMapping("/expenses")
    public ResponseEntity<?> createExpense(@Valid @RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok(createResponse(true, createdExpense, "Expense created successfully"));
    }
    
    @PutMapping("/expenses/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable String id, @Valid @RequestBody Expense expense) {
        try {
            Expense updatedExpense = expenseService.updateExpense(id, expense);
            return ResponseEntity.ok(createResponse(true, updatedExpense, "Expense updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createResponse(false, null, e.getMessage()));
        }
    }
    
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable String id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok(createResponse(true, null, "Expense deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(createResponse(false, null, e.getMessage()));
        }
    }
    
    @GetMapping("/balances")
    public ResponseEntity<?> getBalances() {
        Map<String, BigDecimal> balances = expenseService.calculateBalances();
        return ResponseEntity.ok(createResponse(true, balances, "Balances calculated successfully"));
    }
    
    @GetMapping("/settlements")
    public ResponseEntity<?> getSettlements() {
        List<Map<String, Object>> settlements = expenseService.calculateSettlements();
        return ResponseEntity.ok(createResponse(true, settlements, "Settlements calculated successfully"));
    }
    
    @GetMapping("/people")
    public ResponseEntity<?> getAllPeople() {
        List<Expense> expenses = expenseService.getAllExpenses();
        Map<String, Boolean> people = new HashMap<>();
        
        for (Expense expense : expenses) {
            people.put(expense.getPaidBy(), true);
            if (expense.getParticipants() != null) {
                for (String participant : expense.getParticipants()) {
                    people.put(participant, true);
                }
            }
        }
        
        return ResponseEntity.ok(createResponse(true, people.keySet(), "People retrieved successfully"));
    }
    
    private Map<String, Object> createResponse(boolean success, Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("data", data);
        response.put("message", message);
        return response;
    }
} 