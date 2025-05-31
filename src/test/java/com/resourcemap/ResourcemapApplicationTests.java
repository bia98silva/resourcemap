package com.resourcemap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.rabbitmq.host=localhost",
		"spring.rabbitmq.port=5672"
})
class ResourceMapApplicationTests {

	@Test
	void contextLoads() {
	}
}