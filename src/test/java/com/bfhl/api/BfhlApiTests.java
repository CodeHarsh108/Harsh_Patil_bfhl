package com.bfhl.api;

import com.bfhl.api.dto.BfhlRequest;
import com.bfhl.api.dto.BfhlResponse;
import com.bfhl.api.service.BfhlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BfhlApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BfhlService bfhlService;

    @Autowired
    private ObjectMapper objectMapper;

    // ──────────────────────────────────────────────
    //  Service-layer unit tests
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("Service Layer Tests")
    class ServiceTests {

        @Test
        @DisplayName("Example A – mixed data with numbers, letters, and special chars")
        void exampleA() {
            BfhlRequest req = new BfhlRequest(Arrays.asList("a", "1", "334", "4", "R", "$"));
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.isSuccess()).isTrue();
            assertThat(res.getUserId()).isEqualTo("harsh_patil_06042006");
            assertThat(res.getEmail()).isEqualTo("harshpatil230509@acropolis.in");
            assertThat(res.getRollNumber()).isEqualTo("0827CS231100");
            assertThat(res.getOddNumbers()).containsExactly("1");
            assertThat(res.getEvenNumbers()).containsExactly("334", "4");
            assertThat(res.getAlphabets()).containsExactly("A", "R");
            assertThat(res.getSpecialCharacters()).containsExactly("$");
            assertThat(res.getSum()).isEqualTo("339");
            assertThat(res.getConcatString()).isEqualTo("Ra");
        }

        @Test
        @DisplayName("Example B – multiple alphabets and special chars")
        void exampleB() {
            BfhlRequest req = new BfhlRequest(Arrays.asList("2", "a", "y", "4", "&", "-", "*", "5", "92", "b"));
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.isSuccess()).isTrue();
            assertThat(res.getOddNumbers()).containsExactly("5");
            assertThat(res.getEvenNumbers()).containsExactly("2", "4", "92");
            assertThat(res.getAlphabets()).containsExactly("A", "Y", "B");
            assertThat(res.getSpecialCharacters()).containsExactly("&", "-", "*");
            assertThat(res.getSum()).isEqualTo("103");
            assertThat(res.getConcatString()).isEqualTo("ByA");
        }

        @Test
        @DisplayName("Example C – multi-character alphabetic strings")
        void exampleC() {
            BfhlRequest req = new BfhlRequest(Arrays.asList("A", "ABCD", "DOE"));
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.isSuccess()).isTrue();
            assertThat(res.getOddNumbers()).isEmpty();
            assertThat(res.getEvenNumbers()).isEmpty();
            assertThat(res.getAlphabets()).containsExactly("A", "ABCD", "DOE");
            assertThat(res.getSpecialCharacters()).isEmpty();
            assertThat(res.getSum()).isEqualTo("0");
            assertThat(res.getConcatString()).isEqualTo("EoDdCbAa");
        }

        @Test
        @DisplayName("Empty data array returns zeroed response")
        void emptyData() {
            BfhlRequest req = new BfhlRequest(Collections.emptyList());
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.isSuccess()).isTrue();
            assertThat(res.getOddNumbers()).isEmpty();
            assertThat(res.getEvenNumbers()).isEmpty();
            assertThat(res.getAlphabets()).isEmpty();
            assertThat(res.getSpecialCharacters()).isEmpty();
            assertThat(res.getSum()).isEqualTo("0");
            assertThat(res.getConcatString()).isEmpty();
        }

        @Test
        @DisplayName("Only numbers – no alphabets or special chars")
        void onlyNumbers() {
            BfhlRequest req = new BfhlRequest(Arrays.asList("10", "3", "7", "22"));
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.getOddNumbers()).containsExactly("3", "7");
            assertThat(res.getEvenNumbers()).containsExactly("10", "22");
            assertThat(res.getAlphabets()).isEmpty();
            assertThat(res.getSpecialCharacters()).isEmpty();
            assertThat(res.getSum()).isEqualTo("42");
            assertThat(res.getConcatString()).isEmpty();
        }

        @Test
        @DisplayName("Only special characters")
        void onlySpecialChars() {
            BfhlRequest req = new BfhlRequest(Arrays.asList("@", "#", "!!", "**"));
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.getOddNumbers()).isEmpty();
            assertThat(res.getEvenNumbers()).isEmpty();
            assertThat(res.getAlphabets()).isEmpty();
            assertThat(res.getSpecialCharacters()).containsExactly("@", "#", "!!", "**");
            assertThat(res.getSum()).isEqualTo("0");
            assertThat(res.getConcatString()).isEmpty();
        }

        @Test
        @DisplayName("Single alphabet character")
        void singleAlphabet() {
            BfhlRequest req = new BfhlRequest(List.of("z"));
            BfhlResponse res = bfhlService.processData(req);

            assertThat(res.getAlphabets()).containsExactly("Z");
            assertThat(res.getConcatString()).isEqualTo("Z");
        }
    }

    // ──────────────────────────────────────────────
    //  Controller / Integration tests
    // ──────────────────────────────────────────────

    @Nested
    @DisplayName("Controller Integration Tests")
    class ControllerTests {

        @Test
        @DisplayName("POST /bfhl – Example A returns 200 with correct JSON")
        void postExampleA() throws Exception {
            String requestJson = """
                    { "data": ["a", "1", "334", "4", "R", "$"] }
                    """;

            mockMvc.perform(post("/bfhl")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.is_success").value(true))
                    .andExpect(jsonPath("$.user_id").value("harsh_patil_06042006"))
                    .andExpect(jsonPath("$.email").value("harshpatil230509@acropolis.in"))
                    .andExpect(jsonPath("$.roll_number").value("0827CS231100"))
                    .andExpect(jsonPath("$.odd_numbers[0]").value("1"))
                    .andExpect(jsonPath("$.even_numbers[0]").value("334"))
                    .andExpect(jsonPath("$.even_numbers[1]").value("4"))
                    .andExpect(jsonPath("$.alphabets[0]").value("A"))
                    .andExpect(jsonPath("$.alphabets[1]").value("R"))
                    .andExpect(jsonPath("$.special_characters[0]").value("$"))
                    .andExpect(jsonPath("$.sum").value("339"))
                    .andExpect(jsonPath("$.concat_string").value("Ra"));
        }

        @Test
        @DisplayName("POST /bfhl – missing data field returns 400 with is_success=false")
        void postMissingData() throws Exception {
            mockMvc.perform(post("/bfhl")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.is_success").value(false));
        }

        @Test
        @DisplayName("POST /bfhl – malformed JSON returns 400")
        void postMalformedJson() throws Exception {
            mockMvc.perform(post("/bfhl")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("not json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.is_success").value(false));
        }

        @Test
        @DisplayName("GET /bfhl/health – returns operation_code 1")
        void getOperationCode() throws Exception {
            mockMvc.perform(get("/bfhl/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.operation_code").value(1));
        }
    }
}
