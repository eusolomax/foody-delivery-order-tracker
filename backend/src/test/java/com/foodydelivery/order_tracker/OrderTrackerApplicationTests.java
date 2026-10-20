package com.foodydelivery.order_tracker;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("h2")
@TestPropertySource(properties = {
        "app.jwt.secret=test-jwt-secret-with-at-least-32-characters",
        "app.jwt.expiration=3600000"
})
class OrderTrackerApplicationTests {

	@Test
	void contextLoads() {
	}

}
