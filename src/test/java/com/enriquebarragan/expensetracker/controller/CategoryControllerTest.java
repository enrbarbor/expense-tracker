package com.enriquebarragan.expensetracker.controller;

import com.enriquebarragan.expensetracker.config.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.enriquebarragan.expensetracker.dto.CategoryRequest;
import com.enriquebarragan.expensetracker.model.TransactionType;
import com.enriquebarragan.expensetracker.model.User;
import com.enriquebarragan.expensetracker.repository.UserRepository;
import com.enriquebarragan.expensetracker.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .username("enrique")
                .password(passwordEncoder.encode("123456"))
                .build();
        userRepository.save(user);

        token = jwtService.generateToken("enrique");
    }

    @Test
    void create_shouldCreateCategoryAndReturn201() throws Exception {
        CategoryRequest request = new CategoryRequest("Entertainment", TransactionType.EXPENSE);

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Entertainment"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    void create_withoutTokenShouldReturn403() throws Exception {
        CategoryRequest request = new CategoryRequest("Entertainment", TransactionType.EXPENSE);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withEmptyNameShouldReturn400() throws Exception {
        CategoryRequest request = new CategoryRequest("", TransactionType.EXPENSE);

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}