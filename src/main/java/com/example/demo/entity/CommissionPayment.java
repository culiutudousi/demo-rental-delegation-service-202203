package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommissionPayment {
    @Id
    String id;

    @ManyToOne
    RentalDelegationContract rentalDelegationContract;

    String paymentLink;

    Date createdAt;

    Date expiredAt;
}
