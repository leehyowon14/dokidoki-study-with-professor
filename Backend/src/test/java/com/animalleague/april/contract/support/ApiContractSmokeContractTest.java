package com.animalleague.april.contract.support;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(controllers = ApiContractSmokeContractTest.ContractSmokeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiContractSmokeContractTest extends ApiContractTest {

    @Test
    void mockMvcIsAvailableForContractSuite() {
        assertThat(mockMvc).isNotNull();
    }

    @RestController
    static class ContractSmokeController {

        @GetMapping("/contract-smoke")
        String smoke() {
            return "ok";
        }
    }
}
