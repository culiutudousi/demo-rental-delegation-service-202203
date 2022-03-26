package com.example.demo.repository;

import static com.example.demo.util.UUID.generateUUID;
import com.example.demo.entity.CommissionPayment;
import com.example.demo.entity.RentalDelegationContract;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CommissionPaymentRepositoryTest {

    @Autowired
    RentalDelegationContractRepository contractRepository;

    @Autowired
    CommissionPaymentRepository paymentRepository;

    @Test
    void should_find_by_uuid() {
        // given
        Long contactId = 1L;
        double monthPrice = 3000d;
        RentalDelegationContract contract = contractRepository.save(RentalDelegationContract.builder()
                .id(contactId)
                .monthPrice(BigDecimal.valueOf(monthPrice))
                .build());
        String paymentId = generateUUID();
        String paymentLink = "https://demo.payment.com/payments/12345";
        paymentRepository.save(CommissionPayment.builder()
                .id(paymentId)
                .rentalDelegationContract(contract)
                .paymentLink(paymentLink)
                .build());
        // when
        Optional<CommissionPayment> paymentOptional = paymentRepository.findById(paymentId);
        // then
        Assertions.assertTrue(paymentOptional.isPresent());
        Assertions.assertEquals(contactId, paymentOptional.get().getRentalDelegationContract().getId());
        Assertions.assertEquals(paymentLink, paymentOptional.get().getPaymentLink());
    }
}
