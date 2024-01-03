package com.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
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

    public Mono<String> get_withOptionalQueryParam_withCustomHeader(Optional<String> id) {
        return webClient
                .get()
                .uri(host, uriBuilder -> uriBuilder
                        .queryParamIfPresent("id", id)
                        .build())
                .header("X-Custom-Header", "some-value")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> post_withJsonRequestBody(RequestBody request) {
        return webClient
                .post()
                .uri(host)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchangeToMono(e -> e.bodyToMono(String.class));
    }

    public Mono<String[]> put_withFileRequestBody(File file, String name) {
        return webClient
                .put()
                .uri(host)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(multipart(file, name)))
                .exchangeToMono(e -> e.bodyToMono(String[].class));
    }

    public Mono<String> patch_withFormRequestBody(RequestBody request) {
        return webClient
                .patch()
                .uri(host)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form(request)))
                .exchangeToMono(e -> e.bodyToMono(String.class));
    }

    public Mono<String> delete_withPathSegment(String id) {
        return webClient
                .delete()
                .uri(host, uriBuilder -> uriBuilder
                        .pathSegment(id)
                        .build())
                .exchangeToMono(e -> e.bodyToMono(String.class));
    }

    private MultiValueMap<String, HttpEntity<?>> multipart(File file, String name) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part(name, new FileSystemResource(file));
        return builder.build();
    }

    private MultiValueMap<String, String> form(RequestBody request) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("id", request.id());
        body.add("name", request.name());
        return body;
    }
}
