package com.example.demo.repository;

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
public class RentalDelegationContractRepositoryTest {

    @Autowired
    RentalDelegationContractRepository repository;

    @Test
    void should_find_by_id() {
        // given
        Long id = 1L;
        double monthPrice = 3000d;
        repository.save(RentalDelegationContract.builder()
                .id(id)
                .monthPrice(BigDecimal.valueOf(monthPrice))
                .build());
        // when
        Optional<RentalDelegationContract> contractOptional = repository.findById(id);
        // then
        Assertions.assertTrue(contractOptional.isPresent());
        Assertions.assertEquals(id, contractOptional.get().getId());
        Assertions.assertEquals(0, BigDecimal.valueOf(monthPrice).compareTo(contractOptional.get().getMonthPrice()));
    }
}
