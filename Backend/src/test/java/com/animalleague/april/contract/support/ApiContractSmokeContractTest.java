package com.animalleague.april.contract.support;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    void unsupportedHttpMethodKeeps405Response() throws Exception {
        mockMvc.perform(put("/contract-smoke"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(header().exists("Allow"))
            .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void unsupportedContentTypeKeeps415Response() throws Exception {
        mockMvc.perform(
                post("/contract-smoke")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("plain-text")
            )
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.code").value("UNSUPPORTED_MEDIA_TYPE"));
    }

    @Test
    void missingRouteKeeps404Response() throws Exception {
        mockMvc.perform(get("/missing-contract-smoke"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void missingRequiredHeaderKeeps400Response() throws Exception {
        mockMvc.perform(get("/contract-smoke/header"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }
}
