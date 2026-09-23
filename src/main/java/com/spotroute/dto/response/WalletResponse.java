package com.spotroute.dto.response;

import com.spotroute.persistence.entity.WalletTransaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class WalletResponse {
    private BigDecimal balance;
    private List<TransactionResponse> transactions;

    @Data
    @Builder
    public static class TransactionResponse {
        private String id;
        private BigDecimal amount;
        private WalletTransaction.TransactionType type;
        private String description;
        private String reference;
        private WalletTransaction.TransactionStatus status;
        private LocalDateTime createdAt;

        public static TransactionResponse from(WalletTransaction tx) {
            return TransactionResponse.builder()
                    .id(tx.getId())
                    .amount(tx.getAmount())
                    .type(tx.getType())
                    .description(tx.getDescription())
                    .reference(tx.getReference())
                    .status(tx.getStatus())
                    .createdAt(tx.getCreatedAt())
                    .build();
        }
    }
}
