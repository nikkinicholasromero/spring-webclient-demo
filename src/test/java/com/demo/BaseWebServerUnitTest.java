package com.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

public class BaseWebServerUnitTest {
    protected MockWebServer mockBackEnd;

    protected ObjectMapper objectMapper;

    @BeforeEach
    public void openMocks() throws Exception {
        MockitoAnnotations.openMocks(this).close();

        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
}
