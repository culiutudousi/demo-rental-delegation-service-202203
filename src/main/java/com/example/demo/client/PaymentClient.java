package com.example.demo.client;

import com.example.demo.dto.CreatePaymentRequestDto;
import com.example.demo.dto.CreatePaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "paymentClient", url = "http://localhost:9000")
public interface PaymentClient {

    @RequestMapping(method = RequestMethod.POST, value = "/payments")
    public CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto dto);

    @RequestMapping(method = RequestMethod.GET, value = "/payments/{paymentId}")
    public CreatePaymentResponseDto getPayment(@RequestParam(name = "paymentId") String paymentId);
}
