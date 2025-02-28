package com.sysmapproject.ms_order_service_v1.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwaggerConfigTest {

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCustomOpenAPI_ShouldReturnValidOpenAPIObject() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Order Service API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        assertEquals("API for managing orders", openAPI.getInfo().getDescription());

        Contact contact = openAPI.getInfo().getContact();
        assertNotNull(contact);
        assertEquals("Matheus Silva Lemes", contact.getName());
        assertEquals("matheuslemesmsl@gmail.com", contact.getEmail());
        assertEquals("https://github.com/Matheuslemes", contact.getUrl());
    }
}
