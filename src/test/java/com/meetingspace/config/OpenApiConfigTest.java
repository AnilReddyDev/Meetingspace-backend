
package com.meetingspace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void meetingSpaceOpenAPI_containsBearerAuthAndInfo() {
        OpenApiConfig cfg = new OpenApiConfig();
        OpenAPI api = cfg.meetingSpaceOpenAPI();
        assertNotNull(api.getInfo());
        assertEquals("MeetingSpace Backend API", api.getInfo().getTitle());
        assertTrue(api.getComponents().getSecuritySchemes().containsKey("bearerAuth"));
        SecurityScheme sch = api.getComponents().getSecuritySchemes().get("bearerAuth");
        assertEquals(SecurityScheme.Type.HTTP, sch.getType());
        assertEquals("bearer", sch.getScheme());
        assertEquals("JWT", sch.getBearerFormat());
    }
}
