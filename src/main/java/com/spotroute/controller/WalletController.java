//package com.spotroute.controller;
//
//import com.spotroute.dto.request.PayoutRequest;
//import com.spotroute.dto.response.ApiResponse;
//import com.spotroute.dto.response.WalletResponse;
//import com.spotroute.service.WalletService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/wallet/v1")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('DRIVER')")
//public class WalletController {
//
//    private final WalletService walletService;
//
//    @GetMapping
//    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(
//            @AuthenticationPrincipal UserDetails userDetails) {
//        return ResponseEntity.ok(ApiResponse.ok(walletService.getWallet(userDetails.getUsername())));
//    }
//
//    @PostMapping("/payout")
//    public ResponseEntity<ApiResponse<WalletResponse>> requestPayout(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @Valid @RequestBody PayoutRequest req) {
//        WalletResponse wallet = walletService.requestPayout(
//                userDetails.getUsername(),
//                req.getAmount(),
//                req.getAccountNumber(),
//                req.getBankCode(),
//                req.getAccountName()
//        );
//        return ResponseEntity.ok(ApiResponse.ok("Payout request submitted", wallet));
//    }
//}
