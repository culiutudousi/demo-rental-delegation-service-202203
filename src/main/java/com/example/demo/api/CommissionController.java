package com.example.demo.api;

import com.example.demo.dto.CreatePaymentResponseDto;
import com.example.demo.service.CommissionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rental-delegation-contracts")
@AllArgsConstructor
public class CommissionController {

    private CommissionService commissionService;

    @PostMapping("/{contractId}/payment-request")
    public CreatePaymentResponseDto requestPayment(@PathVariable("contractId") String contactId) throws Exception {
        return commissionService.requestPayment(contactId);
    }
}
