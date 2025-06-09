package com.splitapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "expenses")
public class Expense {
    @Id
    private String id;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Paid by is required")
    private String paidBy;
    
    private List<String> participants;
    private SplitType splitType;
    private List<SplitShare> splitShares;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum SplitType {
        EQUAL,
        PERCENTAGE,
        EXACT_AMOUNT
    }
    
    @Data
    public static class SplitShare {
        private String person;
        private BigDecimal amount;
        private Double percentage;
    }
} 