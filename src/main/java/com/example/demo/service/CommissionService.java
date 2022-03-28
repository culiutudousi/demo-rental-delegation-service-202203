package com.example.demo.service;

import static com.example.demo.util.UUID.generateUUID;
import com.example.demo.client.PaymentClient;
import com.example.demo.dto.CreatePaymentRequestDto;
import com.example.demo.dto.CreatePaymentResponseDto;
import com.example.demo.entity.CommissionPayment;
import com.example.demo.entity.RentalDelegationContract;
import com.example.demo.repository.CommissionPaymentRepository;
import com.example.demo.repository.RentalDelegationContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Service
@AllArgsConstructor
public class CommissionService {

    private CommissionPaymentRepository paymentRepository;

    private RentalDelegationContractRepository contractRepository;

    private PaymentClient paymentClient;

    public CreatePaymentResponseDto requestPayment(String contractId) throws Exception {
        RentalDelegationContract contract = contractRepository.findById(Long.parseLong(contractId))
                .orElseThrow(() -> new Exception(""));
        String paymentId = generateUUID();
        BigDecimal commissionAmount = calculateCommissionByMonthPrice(contract.getMonthPrice());
        CreatePaymentResponseDto paymentResponseDto = paymentClient.createPayment(
                CreatePaymentRequestDto.builder().paymentId(paymentId).amount(commissionAmount).build());
        paymentRepository.save(mapToCommissionPayment(paymentId, contract, paymentResponseDto));
        return paymentResponseDto;
    }

    private BigDecimal calculateCommissionByMonthPrice(BigDecimal monthPrice) {
        return monthPrice.divide(
                BigDecimal.valueOf(2),
                2,
                RoundingMode.DOWN
        );
    }

    private CommissionPayment mapToCommissionPayment(
            String paymentId, RentalDelegationContract contract, CreatePaymentResponseDto paymentResponseDto
    ) {
        return CommissionPayment.builder()
                .id(paymentId)
                .rentalDelegationContract(contract)
                .paymentLink(paymentResponseDto.getPaymentLink())
                .createdAt(new Date())
                .expiredAt(paymentResponseDto.getExpiredAt())
                .build();
    }
}







