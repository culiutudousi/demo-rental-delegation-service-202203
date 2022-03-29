package com.example.demo.integration;

import static com.example.demo.client.Constants.CONTENT_TYPE;
import static com.example.demo.client.Constants.CONTENT_TYPE_VALUE;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.demo.client.PaymentClient;
import com.example.demo.dto.CreatePaymentRequestDto;
import com.example.demo.dto.CreatePaymentResponseDto;
import com.example.demo.entity.CommissionPayment;
import com.example.demo.entity.RentalDelegationContract;
import com.example.demo.repository.CommissionPaymentRepository;
import com.example.demo.repository.RentalDelegationContractRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommissionApiTest {

    @Autowired
    private MockMvc mvc;

    private ClientAndServer server;

    @Autowired
    RentalDelegationContractRepository contractRepository;

    @Autowired
    CommissionPaymentRepository paymentRepository;

    @Autowired
    PaymentClient paymentClient;

    private SimpleDateFormat dateFormatter;

    @BeforeEach
    void init() {
        server = ClientAndServer.startClientAndServer(9000);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @AfterEach
    void restore() {
        server.stop();
    }

    @Test
    void should_create_payment_request() throws Exception {
        // given
        contractRepository.save(RentalDelegationContract.builder()
                .id(1L)
                .monthPrice(BigDecimal.valueOf(3000))
                .build());
        CreatePaymentRequestDto requestBody = CreatePaymentRequestDto.builder()
                .amount(BigDecimal.valueOf(1500))
                .build();
        String paymentLink = "https://demo.payment.com/payments/paymentid";
        Date expiredAt = addMinutes(new Date(), 15);
        CreatePaymentResponseDto responseBody = CreatePaymentResponseDto.builder()
                .paymentId("dummyId")
                .amount(BigDecimal.valueOf(1500))
                .paymentLink(paymentLink)
                .expiredAt(expiredAt)
                .build();
        server
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/payments")
                                .withBody(json("{\"amount\": 1500.0}", MatchType.ONLY_MATCHING_FIELDS))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                                .withBody(new ObjectMapper().writeValueAsString(responseBody))
                );
        // when
        mvc.perform(post("/rental-delegation-contracts/1/payment-request")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentLink").value(paymentLink))
                .andExpect(jsonPath("$.amount").value(1500d))
                .andExpect(jsonPath("$.expiredAt").value(dateFormatter.format(expiredAt)));
        List<CommissionPayment> payments = paymentRepository.findAll();
        assertEquals(1, payments.size());
        assertEquals(1L, payments.get(0).getRentalDelegationContract().getId());
        assertEquals(paymentLink, payments.get(0).getPaymentLink());
        assertEquals(dateFormatter.format(expiredAt), dateFormatter.format(payments.get(0).getExpiredAt()));
        assertNotNull(payments.get(0).getId());
    }
}
