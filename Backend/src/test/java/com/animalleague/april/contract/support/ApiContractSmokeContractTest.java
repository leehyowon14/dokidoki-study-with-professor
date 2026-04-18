package com.animalleague.april.contract.support;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContractSmokeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiContractSmokeContractTest extends ApiContractTest {

    @Test
    void mockMvcIsAvailableForContractSuite() throws Exception {
        assertThat(mockMvc).isNotNull();

        mockMvc.perform(get("/contract-smoke"))
            .andExpect(status().isOk())
            .andExpect(content().string("ok"));
    }
}
