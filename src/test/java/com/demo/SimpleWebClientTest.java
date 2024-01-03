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

import java.io.File;
import java.io.IOException;
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
    public void get_withOptionalQueryParam_withQueryParam_withoutQueryParam() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        assertEquals(target.get_withOptionalQueryParam_withCustomHeader(Optional.empty()).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
        assertThat(recordedRequest.getHeader("X-Custom-Header")).isEqualTo("some-value");
    }

    @Test
    public void get_withOptionalQueryParam_withQueryParam_withQueryParam() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String id = UUID.randomUUID().toString();

        assertEquals(target.get_withOptionalQueryParam_withCustomHeader(Optional.of(id)).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.GET.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/?id=" + id);
        assertThat(recordedRequest.getHeader("X-Custom-Header")).isEqualTo("some-value");
    }

    @Test
    public void post_withRequestBody() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String id = UUID.randomUUID().toString();
        String name = "Nikki Nicholas Romero";
        RequestBody request = new RequestBody(id, name);

        assertEquals(target.post_withJsonRequestBody(request).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.POST.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(objectMapper.readValue(recordedRequest.getBody().readUtf8(), RequestBody.class)).isEqualTo(request);
    }

    @Test
    public void put_withFileRequestBody() throws InterruptedException, IOException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String fileName = UUID.randomUUID().toString();
        File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + fileName + ".txt");
        boolean fileCreated = file.createNewFile();
        assertThat(fileCreated).isTrue();

        assertEquals(target.put_withFileRequestBody(file, "file").block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.PUT.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).startsWith(MediaType.MULTIPART_FORM_DATA_VALUE);
        // TODO: Verify request body
    }

    @Test
    public void patch_withFormRequestBody() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setBody("Hello, World")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String id = UUID.randomUUID().toString();
        String name = "Nikki Nicholas Romero";
        RequestBody request = new RequestBody(id, name);

        assertEquals(target.patch_withFormRequestBody(request).block(), "Hello, World");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo(HttpMethod.PATCH.toString());
        assertThat(recordedRequest.getPath()).isEqualTo("/");
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        Assertions.assertThat(recordedRequest.getBody().readUtf8()).isEqualTo("id=" + id + "&name=Nikki+Nicholas+Romero");
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
