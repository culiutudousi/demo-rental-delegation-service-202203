package com.example.demo.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentalDelegationContract {
    @Id
    @GeneratedValue
    Long id;

    BigDecimal monthPrice;

    Long clientId;

    Long agentId;

    Long houseId;

    Date createdAt;
}
