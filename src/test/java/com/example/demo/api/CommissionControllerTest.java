package com.example.demo.api;

import static org.apache.commons.lang3.time.DateUtils.addMinutes;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.demo.dto.CreatePaymentResponseDto;
import com.example.demo.service.CommissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommissionController.class)
public class CommissionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommissionService commissionService;

    private SimpleDateFormat dateFormatter;

    @BeforeEach
    void init() {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    void should_create_payment_request() throws Exception {
        // given
        String paymentId = "9DEEF2A86EB0404AACE2F3B710195FC3";
        String paymentLink = "https://demo.payment.com/payments/paymentid";
        BigDecimal amount = BigDecimal.valueOf(1500);
        Date expiredAt = addMinutes(new Date(), 15);
        doReturn(CreatePaymentResponseDto.builder().paymentId(paymentId).paymentLink(paymentLink).amount(amount).expiredAt(expiredAt).build())
                .when(commissionService).requestPayment("1");
        // when
        mvc.perform(post("/rental-delegation-contracts/1/payment-request")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId))
                .andExpect(jsonPath("$.paymentLink").value(paymentLink))
                .andExpect(jsonPath("$.amount").value(1500d))
                .andExpect(jsonPath("$.expiredAt").value(dateFormatter.format(expiredAt)));
    }
}
