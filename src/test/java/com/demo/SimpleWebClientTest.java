package com.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleWebClientTest extends BaseWebServerUnitTest {
    @InjectMocks
    private SimpleWebClient target;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(target, "host", "http://localhost:" + mockBackEnd.getPort());
    }

    @Test
    public void get_withOptionalQueryParam_withoutQueryParam() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        assertEquals(target.get_withOptionalQueryParam(Optional.empty()).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
    }

    @Test
    public void get_withOptionalQueryParam_withQueryParam() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String id = UUID.randomUUID().toString();

        assertEquals(target.get_withOptionalQueryParam(Optional.of(id)).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/?id=" + id);
    }

    @Test
    public void post_withRequestBody() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String id = UUID.randomUUID().toString();
        String name = "Nikki Nicholas Romero";
        RequestBody request = new RequestBody(id, name);

        assertEquals(target.post_withJsonRequestBody(Mono.just(request)).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
        assertThat(objectMapper.readValue(recordedRequest.getBody().readUtf8(), RequestBody.class)).isEqualTo(request);
    }

    @Test
    public void put() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        assertEquals(target.put().block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.PUT.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
    }

    @Test
    public void patch() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        assertEquals(target.patch().block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.PATCH.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
    }

    @Test
    public void delete_withPathSegment() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String id = UUID.randomUUID().toString();

        assertEquals(target.delete_withPathSegment(id).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.DELETE.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/" + id);
    }
}
