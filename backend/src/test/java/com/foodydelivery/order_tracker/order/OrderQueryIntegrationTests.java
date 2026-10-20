package com.foodydelivery.order_tracker.order;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class OrderQueryIntegrationTests {

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
    void shouldListOnlyAuthenticatedUserOrders() throws Exception {
        User firstUser = createUser("First User");
        User secondUser = createUser("Second User");

        createOrder(firstUser, "first-order");
        createOrder(secondUser, "second-order");

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(firstUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerName").value("First User"));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoOrders() throws Exception {
        User user = createUser("Empty User");

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldFindOrderOwnedByAuthenticatedUser() throws Exception {
        User user = createUser("Order Owner");
        createOrder(user, "find-order");
        Order order = orderRepository.findByIdempotencyKey("find-order").orElseThrow();

        mockMvc.perform(get("/api/orders/{uuid}", order.getUuid())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(order.getUuid().toString()))
                .andExpect(jsonPath("$.customerName").value("Order Owner"));
    }

    @Test
    void shouldReturnNotFoundForMissingOrder() throws Exception {
        User user = createUser("Missing Order User");

        mockMvc.perform(get("/api/orders/{uuid}", UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForOrderOwnedByAnotherUser() throws Exception {
        User owner = createUser("Owner User");
        User requester = createUser("Requester User");
        createOrder(owner, "private-order");
        Order order = orderRepository.findByIdempotencyKey("private-order").orElseThrow();

        mockMvc.perform(get("/api/orders/{uuid}", order.getUuid())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(requester)))
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

    private void createOrder(User user, String key) throws Exception {
        mockMvc.perform(post("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(user))
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [{"productName": "Pizza", "quantity": 1}]
                                }
                                """))
                .andExpect(status().isCreated());
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtService.generateToken(user);
    }
}
