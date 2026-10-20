package com.foodydelivery.order_tracker.auth;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.foodydelivery.order_tracker.auth.service.JwtService;
import com.foodydelivery.order_tracker.user.repository.UserRepository;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@TestPropertySource(properties = {
        "app.jwt.secret=test-jwt-secret-with-at-least-32-characters",
        "app.jwt.expiration=3600000"
})
class AuthenticationIntegrationTests {

    private static final String PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserWithoutReturningPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("bruno@example.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("bruno@example.com"))
                .andExpect(jsonPath("$.deliveryAddress").value("Rua A, 10"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldHashPasswordBeforePersistence() throws Exception {
        register("hash@example.com");

        String storedPassword = userRepository.findByEmail("hash@example.com")
                .orElseThrow()
                .getPassword();

        org.junit.jupiter.api.Assertions.assertNotEquals(PASSWORD, storedPassword);
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(PASSWORD, storedPassword));
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        register("duplicate@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("duplicate@example.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldLoginAndReturnJwt() throws Exception {
        register("login@example.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("login@example.com", PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", not(emptyString())))
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }

    @Test
    void shouldRejectInvalidCredentialsWithoutRevealingUserExistence() throws Exception {
        register("invalid@example.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("invalid@example.com", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void shouldProtectNonAuthEndpoints() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAcceptValidJwtOnProtectedEndpoints() throws Exception {
        register("protected@example.com");

        String token = jwtService.generateToken(userRepository.findByEmail("protected@example.com").orElseThrow());

        mockMvc.perform(get("/api/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isMethodNotAllowed());
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(email)))
                .andExpect(status().isCreated());
    }

    private String registerJson(String email) {
        return """
                {
                  "name": "Ana Silva",
                  "email": "%s",
                  "password": "%s",
                  "deliveryAddress": "Rua A, 10"
                }
                """.formatted(email, PASSWORD);
    }

    private String loginJson(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
    }
}
