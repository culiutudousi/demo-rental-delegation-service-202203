package com.example.demo.client;

import static com.example.demo.client.Constants.CONTENT_TYPE;
import static com.example.demo.client.Constants.CONTENT_TYPE_VALUE;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import com.example.demo.dto.CreatePaymentRequestDto;
import com.example.demo.dto.CreatePaymentResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
public class PaymentClientTest {

    private ClientAndServer server;

    @Autowired
    PaymentClient paymentClient;

    @BeforeEach
    void startFakeServer() {
        server = ClientAndServer.startClientAndServer(9000);
    }

    @AfterEach
    void stopFakeServer() {
        server.stop();
    }

    @Test
    void should_create_payment() throws JsonProcessingException {
        // given
        CreatePaymentRequestDto requestBody = CreatePaymentRequestDto.builder()
                .paymentId("abcde")
                .amount(BigDecimal.valueOf(3000))
                .build();
        CreatePaymentResponseDto responseBody = CreatePaymentResponseDto.builder()
                .paymentId("abcde")
                .amount(BigDecimal.valueOf(3000))
                .paymentLink("https://payment.demo.com/payments/abcde")
                .expiredAt(addMinutes(new Date(), 15))
                .build();
        server
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/payments")
                                .withBody(new ObjectMapper().writeValueAsString(requestBody))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                                .withBody(new ObjectMapper().writeValueAsString(responseBody))
                );
        // when
        CreatePaymentResponseDto createPaymentResponseDto = paymentClient.createPayment(requestBody);
        // then
        Assertions.assertEquals(createPaymentResponseDto.getPaymentId(), requestBody.getPaymentId());
        Assertions.assertEquals(createPaymentResponseDto.getAmount(), requestBody.getAmount());
        Assertions.assertEquals(createPaymentResponseDto.getPaymentLink(), responseBody.getPaymentLink());
        Assertions.assertEquals(createPaymentResponseDto.getExpiredAt(), responseBody.getExpiredAt());
    }

    @Test
    void should_get_payment() throws JsonProcessingException {
        // given
        String paymentId = "abcde";
        CreatePaymentResponseDto responseBody = CreatePaymentResponseDto.builder()
                .paymentId(paymentId)
                .amount(BigDecimal.valueOf(3000))
                .paymentLink("https://payment.demo.com/payments/abcde")
                .expiredAt(addMinutes(new Date(), 15))
                .build();
        server
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/payments/" + paymentId)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                                .withBody(new ObjectMapper().writeValueAsString(responseBody))
                );
        // when
        CreatePaymentResponseDto paymentResponseDto = paymentClient.getPayment(paymentId);
        // then
        Assertions.assertEquals(paymentId, paymentResponseDto.getPaymentId());
        Assertions.assertEquals(responseBody.getAmount(), paymentResponseDto.getAmount());
        Assertions.assertEquals(responseBody.getPaymentLink(), paymentResponseDto.getPaymentLink());
        Assertions.assertEquals(responseBody.getExpiredAt().toString(), paymentResponseDto.getExpiredAt().toString());
    }
}
