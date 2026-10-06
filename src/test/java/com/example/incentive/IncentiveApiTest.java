package com.example.incentive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IncentiveApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsCreatorStats() throws Exception {
        mockMvc.perform(get("/creators/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creatorId").value(1))
                .andExpect(jsonPath("$.postsLast7Days").value(4))
                .andExpect(jsonPath("$.activeDaysLast7Days").value(3))
                .andExpect(jsonPath("$.validPostsLast7Days").value(3));
    }

    @Test
    void createsAndStoresDecision() throws Exception {
        mockMvc.perform(post("/incentive-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creatorId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recommended").value(true))
                .andExpect(jsonPath("$.recommendation").value("建议激励"));
    }

    @Test
    void rejectsMissingCreatorId() throws Exception {
        mockMvc.perform(post("/incentive-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void returns404ForUnknownCreator() throws Exception {
        mockMvc.perform(get("/creators/999/stats"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
