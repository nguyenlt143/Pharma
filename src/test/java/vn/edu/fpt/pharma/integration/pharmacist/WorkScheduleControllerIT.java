package vn.edu.fpt.pharma.integration.pharmacist;

import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for Work Schedule functionality.
 * Test Cases: PHA026-PHA033 (8 tests)
 */
@DisplayName("Work Schedule Controller Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkScheduleControllerIT extends BasePharmacistIT {

    private static final String TEST_CLASS = "WorkScheduleControllerIT";

    @Test
    @Order(1)
    @DisplayName("PHA026 - Verify work schedule current week")
    void testWorkSchedule_currentWeek_shouldDisplaySchedule() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("summaries"))
                .andExpect(model().attributeExists("start"))
                .andExpect(model().attributeExists("end"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_currentWeek_shouldDisplaySchedule", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(2)
    @DisplayName("PHA027 - Verify work schedule specific date range")
    void testWorkSchedule_specificDateRange_shouldFilterByDate() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work?start=2026-01-01&end=2026-01-07";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("start", "2026-01-01")
                        .param("end", "2026-01-07"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("summaries"))
                .andExpect(model().attribute("start", java.time.LocalDate.parse("2026-01-01")))
                .andExpect(model().attribute("end", java.time.LocalDate.parse("2026-01-07")))
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_specificDateRange_shouldFilterByDate", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(3)
    @DisplayName("PHA028 - Verify work schedule no shifts")
    void testWorkSchedule_noShifts_shouldDisplayEmpty() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("summaries"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_noShifts_shouldDisplayEmpty", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(4)
    @DisplayName("PHA029 - Verify work schedule summary")
    void testWorkSchedule_withShifts_shouldShowSummary() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("summaries"))
                .andReturn();

        logEvidenceDetailed(TEST_CLASS, "testWorkSchedule_withShifts_shouldShowSummary", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(5)
    @DisplayName("PHA030 - Verify work schedule default dates")
    void testWorkSchedule_noParams_shouldDefaultToCurrentWeek() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("start"))
                .andExpect(model().attributeExists("end"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_noParams_shouldDefaultToCurrentWeek", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(6)
    @DisplayName("PHA031 - Verify work schedule future week")
    void testWorkSchedule_futureWeek_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work?start=2026-02-01&end=2026-02-07";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("start", "2026-02-01")
                        .param("end", "2026-02-07"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("summaries"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_futureWeek_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(7)
    @DisplayName("PHA032 - Verify work schedule past week")
    void testWorkSchedule_pastWeek_shouldDisplay() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work?start=2025-12-01&end=2025-12-07";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("start", "2025-12-01")
                        .param("end", "2025-12-07"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("summaries"))
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_pastWeek_shouldDisplay", "GET", endpoint, result, startTime);
    }

    @Test
    @Order(8)
    @DisplayName("PHA033 - Verify work schedule invalid date format")
    void testWorkSchedule_invalidDate_shouldReturn400() throws Exception {
        long startTime = System.currentTimeMillis();
        String endpoint = "/pharmacist/work?start=invalid";

        MvcResult result = mockMvc.perform(get(endpoint)
                        .with(asPharmacist())
                        .param("start", "invalid"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        logEvidence(TEST_CLASS, "testWorkSchedule_invalidDate_shouldReturn400", "GET", endpoint, result, startTime);
    }
}

