package com.splitapp.service;

import com.splitapp.model.Expense;
import com.splitapp.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public Expense createExpense(Expense expense) {
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        
        // Set default split type if not provided
        if (expense.getSplitType() == null) {
            expense.setSplitType(Expense.SplitType.EQUAL);
        }
        
        // Calculate split shares
        calculateSplitShares(expense);
        
        return expenseRepository.save(expense);
    }
    
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }
    
    public Expense updateExpense(String id, Expense updatedExpense) {
        return expenseRepository.findById(id)
            .map(existingExpense -> {
                existingExpense.setAmount(updatedExpense.getAmount());
                existingExpense.setDescription(updatedExpense.getDescription());
                existingExpense.setPaidBy(updatedExpense.getPaidBy());
                existingExpense.setParticipants(updatedExpense.getParticipants());
                existingExpense.setSplitType(updatedExpense.getSplitType());
                existingExpense.setUpdatedAt(LocalDateTime.now());
                
                calculateSplitShares(existingExpense);
                return expenseRepository.save(existingExpense);
            })
            .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }
    
    public void deleteExpense(String id) {
        expenseRepository.deleteById(id);
    }
    
    public Map<String, BigDecimal> calculateBalances() {
        List<Expense> expenses = expenseRepository.findAll();
        Map<String, BigDecimal> balances = new HashMap<>();
        
        for (Expense expense : expenses) {
            // Add amount to payer's balance
            balances.merge(expense.getPaidBy(), expense.getAmount(), BigDecimal::add);
            
            // Subtract shares from participants' balances
            for (Expense.SplitShare share : expense.getSplitShares()) {
                balances.merge(share.getPerson(), share.getAmount().negate(), BigDecimal::add);
            }
        }
        
        return balances;
    }
    
    public List<Map<String, Object>> calculateSettlements() {
        Map<String, BigDecimal> balances = calculateBalances();
        List<Map<String, Object>> settlements = new ArrayList<>();
        
        // Separate debtors and creditors
        Map<String, BigDecimal> debtors = new HashMap<>();
        Map<String, BigDecimal> creditors = new HashMap<>();
        
        balances.forEach((person, balance) -> {
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                debtors.put(person, balance.abs());
            } else if (balance.compareTo(BigDecimal.ZERO) > 0) {
                creditors.put(person, balance);
            }
        });
        
        // Calculate settlements
        for (Map.Entry<String, BigDecimal> debtor : debtors.entrySet()) {
            BigDecimal remainingDebt = debtor.getValue();
            
            for (Map.Entry<String, BigDecimal> creditor : creditors.entrySet()) {
                if (remainingDebt.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal creditorAmount = creditor.getValue();
                if (creditorAmount.compareTo(BigDecimal.ZERO) <= 0) continue;
                
                BigDecimal settlementAmount = remainingDebt.min(creditorAmount);
                
                Map<String, Object> settlement = new HashMap<>();
                settlement.put("from", debtor.getKey());
                settlement.put("to", creditor.getKey());
                settlement.put("amount", settlementAmount);
                settlements.add(settlement);
                
                remainingDebt = remainingDebt.subtract(settlementAmount);
                creditor.setValue(creditorAmount.subtract(settlementAmount));
            }
        }
        
        return settlements;
    }
    
    private void calculateSplitShares(Expense expense) {
        List<Expense.SplitShare> shares = new ArrayList<>();
        
        switch (expense.getSplitType()) {
            case EQUAL:
                BigDecimal shareAmount = expense.getAmount()
                    .divide(BigDecimal.valueOf(expense.getParticipants().size()), 2, RoundingMode.HALF_UP);
                
                for (String participant : expense.getParticipants()) {
                    Expense.SplitShare share = new Expense.SplitShare();
                    share.setPerson(participant);
                    share.setAmount(shareAmount);
                    shares.add(share);
                }
                break;
                
            case PERCENTAGE:
                for (Expense.SplitShare share : expense.getSplitShares()) {
                    BigDecimal amount = expense.getAmount()
                        .multiply(BigDecimal.valueOf(share.getPercentage() / 100))
                        .setScale(2, RoundingMode.HALF_UP);
                    share.setAmount(amount);
                    shares.add(share);
                }
                break;
                
            case EXACT_AMOUNT:
                shares = expense.getSplitShares();
                break;
        }
        
        expense.setSplitShares(shares);
    }
} 