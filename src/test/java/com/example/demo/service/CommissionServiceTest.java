package com.example.demo.service;

import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import com.example.demo.client.PaymentClient;
import com.example.demo.dto.CreatePaymentRequestDto;
import com.example.demo.dto.CreatePaymentResponseDto;
import com.example.demo.entity.CommissionPayment;
import com.example.demo.entity.RentalDelegationContract;
import com.example.demo.repository.CommissionPaymentRepository;
import com.example.demo.repository.RentalDelegationContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class CommissionServiceTest {

    private CommissionService commissionService;

    @Mock
    private CommissionPaymentRepository paymentRepository;

    @Mock
    private RentalDelegationContractRepository contractRepository;

    @Mock
    private PaymentClient paymentClient;

    @Captor
    ArgumentCaptor<CreatePaymentRequestDto> paymentRequestDtoCaptor;

    @Captor
    ArgumentCaptor<CommissionPayment> commissionPaymentCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commissionService = new CommissionService(paymentRepository, contractRepository, paymentClient);
    }

    @Test
    void should_create_payment_request() throws Exception {
        // given
        doReturn(Optional.of(RentalDelegationContract.builder().id(1L).monthPrice(BigDecimal.valueOf(3000)).build()))
                .when(contractRepository).findById(1L);
        String paymentId = "9DEEF2A86EB0404AACE2F3B710195FC3";
        String paymentLink = "https://demo.payment.com/payments/paymentid";
        Date expiredAt = addMinutes(new Date(), 15);
        BigDecimal commissionAmount = BigDecimal.valueOf(1500);
        doReturn(CreatePaymentResponseDto.builder().paymentId(paymentId).paymentLink(paymentLink).expiredAt(expiredAt).amount(commissionAmount).build())
                .when(paymentClient).createPayment(any());
        // when
        CreatePaymentResponseDto paymentResponseDto = commissionService.requestPayment("1");
        // then
        verify(paymentClient).createPayment(paymentRequestDtoCaptor.capture());
        assertEquals(0, commissionAmount.compareTo(paymentRequestDtoCaptor.getValue().getAmount()));
        verify(paymentRepository).save(commissionPaymentCaptor.capture());
        assertEquals(1L, commissionPaymentCaptor.getValue().getRentalDelegationContract().getId());
        assertEquals(paymentLink, commissionPaymentCaptor.getValue().getPaymentLink());
        assertEquals(expiredAt, commissionPaymentCaptor.getValue().getExpiredAt());
        assertEquals(paymentLink, paymentResponseDto.getPaymentLink());
        assertEquals(expiredAt, paymentResponseDto.getExpiredAt());
        assertEquals(0, commissionAmount.compareTo(paymentResponseDto.getAmount()));
    }
}
