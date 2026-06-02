package com.group3.controllers;

import com.group3.dto.request.MomoCreatePaymentRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.MomoCreatePaymentResponse;
import com.group3.service.MomoService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ApiPaymentController {

    @Autowired
    private MomoService momoService;

    @PostMapping("/api/secure/payments/momo")
    public ResponseEntity<ApiResponse<MomoCreatePaymentResponse>> createMomoPayment(
            Principal principal,
            @Valid @RequestBody MomoCreatePaymentRequest request) {
        MomoCreatePaymentResponse response = momoService.createPayment(request, principal);
        return ResponseEntity.ok(new ApiResponse<>(200, "Tạo thanh toán MoMo thành công", response));
    }

    @PostMapping("/api/payment/momo/ipn")
    public ResponseEntity<Map<String, Object>> momoIpn(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(momoService.handleIpn(payload));
    }

    @GetMapping("/api/payment/momo/redirect")
    public ResponseEntity<Map<String, Object>> momoRedirect(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(momoService.handleRedirect(params));
    }
}
