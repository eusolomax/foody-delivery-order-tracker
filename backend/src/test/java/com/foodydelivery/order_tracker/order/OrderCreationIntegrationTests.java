package com.foodydelivery.order_tracker.order;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.foodydelivery.order_tracker.auth.service.JwtService;
import com.foodydelivery.order_tracker.order.entity.Order;
import com.foodydelivery.order_tracker.order.repository.OrderRepository;
import com.foodydelivery.order_tracker.user.entity.User;
import com.foodydelivery.order_tracker.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestPropertySource(properties = {
        "app.jwt.secret=test-jwt-secret-with-at-least-32-characters",
        "app.jwt.expiration=3600000"
})
class OrderCreationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldCreateOrderWithAllItems() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user))
                        .header("idempotency-key", "create-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.customerName").value("Ana Silva"))
                .andExpect(jsonPath("$.deliveryAddress").value("Rua do Cliente, 10"))
                .andExpect(jsonPath("$.status").value("RECEBIDO"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].productName").value("Pizza"))
                .andExpect(jsonPath("$.items[1].quantity").value(1));

        Order savedOrder = orderRepository.findByIdempotencyKey("create-key").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(2, savedOrder.getItems().size());
        org.junit.jupiter.api.Assertions.assertTrue(savedOrder.getItems().stream()
                .allMatch(item -> item.getOrder() == savedOrder));
    }

    @Test
    void shouldRejectUnauthenticatedOrderCreation() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidOrderRequest() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user))
                .header("Idempotency-Key", "invalid-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrderJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("productName: Product name is required")));

        org.junit.jupiter.api.Assertions.assertTrue(orderRepository.findAll().isEmpty());
    }

    @Test
    void shouldReplaySameRequestWithoutDuplicatingOrder() throws Exception {
        User user = createUser();
        String token = bearerToken(user);

        createOrder(token, "replay-key", validOrderJson()).andExpect(status().isCreated());
        Order first = orderRepository.findByIdempotencyKey("replay-key").orElseThrow();

        createOrder(token, "replay-key", validOrderJson())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(first.getUuid().toString()));

        org.junit.jupiter.api.Assertions.assertEquals(1, orderRepository.count());
    }

    private org.springframework.test.web.servlet.ResultActions createOrder(
            String token,
            String idempotencyKey,
            String body
    ) throws Exception {
        return mockMvc.perform(post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private User createUser() {
        return userRepository.save(new User(
                "Ana Silva",
                "ana-" + UUID.randomUUID() + "@example.com",
                "Rua do Cliente, 10",
                "hashed-password"
        ));
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtService.generateToken(user);
    }

    private String validOrderJson() {
        return """
                {
                  "items": [
                    {"productName": "Pizza", "quantity": 2},
                    {"productName": "Refrigerante", "quantity": 1}
                  ]
                }
                """;
    }

    private String invalidOrderJson() {
        return """
                {
                  "items": [
                    {"productName": "", "quantity": 0}
                  ]
                }
                """;
    }

    private String differentOrderJson() {
        return """
                {
                  "items": [
                    {"productName": "Pizza", "quantity": 3}
                  ]
                }
                """;
    }
}
