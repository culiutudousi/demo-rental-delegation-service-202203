package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentResponseDto {
    String paymentId;
    String paymentLink;
    BigDecimal amount;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date expiredAt;
}
