package models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Transaction {
    private long id;
    private long accountId;
    private long assetId;
    private TransactionType type;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private LocalDateTime transactionDate;
    private BigDecimal commission;

    public enum TransactionType {
        BUY, SELL
    }
}