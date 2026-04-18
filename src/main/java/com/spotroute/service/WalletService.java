package com.spotroute.service;

import com.spotroute.dto.response.WalletResponse;
import com.spotroute.entity.DriverProfile;
import com.spotroute.entity.WalletTransaction;
import com.spotroute.exception.BadRequestException;
import com.spotroute.exception.ForbiddenException;
import com.spotroute.repository.DriverProfileRepository;
import com.spotroute.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final DriverProfileRepository driverProfileRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletResponse getWallet(String userEmail) {
        DriverProfile driver = getDriverByEmail(userEmail);

        List<WalletResponse.TransactionResponse> transactions =
                walletTransactionRepository.findByDriverIdOrderByCreatedAtDesc(driver.getId())
                        .stream()
                        .map(WalletResponse.TransactionResponse::from)
                        .toList();

        return WalletResponse.builder()
                .balance(driver.getWalletBalance())
                .transactions(transactions)
                .build();
    }

    @Transactional
    public WalletResponse requestPayout(String userEmail, BigDecimal amount,
                                        String accountNumber, String bankCode, String accountName) {
        DriverProfile driver = getDriverByEmail(userEmail);

        if (driver.getWalletBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient wallet balance");
        }

        // Deduct from wallet
        driver.setWalletBalance(driver.getWalletBalance().subtract(amount));
        driverProfileRepository.save(driver);

        // Record transaction
        WalletTransaction tx = WalletTransaction.builder()
                .driver(driver)
                .amount(amount)
                .type(WalletTransaction.TransactionType.DEBIT)
                .description("Payout to " + accountName + " (" + accountNumber + ")")
                .reference("PAYOUT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(WalletTransaction.TransactionStatus.PENDING)
                .build();
        walletTransactionRepository.save(tx);

        return getWallet(userEmail);
    }

    @Transactional
    public void creditDriver(DriverProfile driver, BigDecimal amount, String description, String reference) {
        driver.setWalletBalance(driver.getWalletBalance().add(amount));
        driverProfileRepository.save(driver);

        WalletTransaction tx = WalletTransaction.builder()
                .driver(driver)
                .amount(amount)
                .type(WalletTransaction.TransactionType.CREDIT)
                .description(description)
                .reference(reference)
                .status(WalletTransaction.TransactionStatus.COMPLETED)
                .build();
        walletTransactionRepository.save(tx);
    }

    private DriverProfile getDriverByEmail(String email) {
        return driverProfileRepository.findAll().stream()
                .filter(dp -> dp.getUser().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("Driver profile not found"));
    }
}
