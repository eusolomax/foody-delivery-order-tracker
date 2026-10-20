package com.foodydelivery.order_tracker.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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

import com.foodydelivery.order_tracker.auth.service.JwtService;
import com.foodydelivery.order_tracker.order.entity.Order;
import com.foodydelivery.order_tracker.order.entity.OrderStatus;
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
class OrderStatusIntegrationTests {

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
    void shouldUpdateStatusAndPersistIt() throws Exception {
        User user = createUser("Status Owner");
        Order order = createOrder(user, "status-update");

        updateStatus(user, order, "EM_PREPARO")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_PREPARO"));

        Order persisted = orderRepository.findById(order.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(OrderStatus.EM_PREPARO, persisted.getStatus());
    }

    @Test
    void shouldRejectInvalidStatusTransition() throws Exception {
        User user = createUser("Invalid Transition Owner");
        Order order = createOrder(user, "invalid-transition");

        updateStatus(user, order, "ENTREGUE")
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundForMissingOrder() throws Exception {
        User user = createUser("Missing Status Owner");

        mockMvc.perform(patch("/api/orders/{uuid}/status", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"EM_PREPARO\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotUpdateAnotherUsersOrder() throws Exception {
        User owner = createUser("Order Owner");
        User otherUser = createUser("Other User");
        Order order = createOrder(owner, "private-status");

        updateStatus(otherUser, order, "EM_PREPARO")
                .andExpect(status().isNotFound());
    }

    private User createUser(String name) {
        return userRepository.save(new User(
                name,
                name.toLowerCase().replace(" ", "-") + "-" + UUID.randomUUID() + "@example.com",
                "Rua do Cliente, 10",
                "hashed-password"
        ));
    }

    private Order createOrder(User user, String key) throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user))
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productName\":\"Pizza\",\"quantity\":1}]}"))
                .andExpect(status().isCreated());
        return orderRepository.findByIdempotencyKey(key).orElseThrow();
    }

    private org.springframework.test.web.servlet.ResultActions updateStatus(
            User user,
            Order order,
            String status
    ) throws Exception {
        return mockMvc.perform(patch("/api/orders/{uuid}/status", order.getUuid())
                .header(HttpHeaders.AUTHORIZATION, bearerToken(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"" + status + "\"}"));
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtService.generateToken(user);
    }
}
