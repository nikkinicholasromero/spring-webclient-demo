package com.demo;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Optional;

@Component
public class SimpleWebClient {
    private final String host;
    private final WebClient webClient;

    public SimpleWebClient(@Value("${host}") String host) {
        this.host = host;
        this.webClient = WebClient.create();
    }

    public Mono<String> get_withOptionalQueryParam(Optional<String> id) {
        return webClient
                .get()
                .uri(host, uriBuilder -> uriBuilder
                        .queryParamIfPresent("id", id)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> post_withJsonRequestBody(Publisher<RequestBody> request) {
        return webClient
                .post()
                .uri(host)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(request, RequestBody.class))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> put_withFileRequestBody(File file, String name) {
        return webClient
                .put()
                .uri(host)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fromFile(file, name)))
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> patch_withCustomHeader() {
        return webClient
                .patch()
                .uri(host)
                .header("X-Custom-Header", "some-value")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> delete_withPathSegment(String id) {
        return webClient
                .delete()
                .uri(host, uriBuilder -> uriBuilder
                        .pathSegment(id)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    private MultiValueMap<String, HttpEntity<?>> fromFile(File file, String name) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part(name, new FileSystemResource(file));
        return builder.build();
    }
}
