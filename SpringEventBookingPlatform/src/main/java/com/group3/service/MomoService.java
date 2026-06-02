package com.group3.service;

import com.group3.dto.request.MomoCreatePaymentRequest;
import com.group3.dto.response.MomoCreatePaymentResponse;
import java.security.Principal;
import java.util.Map;

public interface MomoService {

    MomoCreatePaymentResponse createPayment(MomoCreatePaymentRequest request, Principal principal);

    Map<String, Object> handleIpn(Map<String, Object> payload);

    Map<String, Object> handleRedirect(Map<String, String> params);
}
