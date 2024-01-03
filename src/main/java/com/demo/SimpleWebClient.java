package com.demo;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public Mono<String> put_withCustomHeader() {
        return webClient
                .put()
                .uri(host)
                .header("X-Custom-Header", "some-value")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> patch() {
        return webClient
                .patch()
                .uri(host)
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
}
