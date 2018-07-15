package com.eockim.webflux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@SpringBootApplication
@RestController
public class WebfluxApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }
}

@Component
class BabyHandler {

    public Mono<ServerResponse> baby(ServerRequest request) {

        Baby baby = Stream.of(new Baby("mj", 1), new Baby("sw", 1), new Baby("yr", 33))
                .filter(s -> s.getName().equals(request.pathVariable("name")))
                .findFirst()
                .orElse(new Baby("", 0));

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(Mono.just(baby), Baby.class);
    }

    public Mono<ServerResponse> babyList(ServerRequest request) {

        Flux<Baby> babyFlux = Flux.just(new Baby("mj", 1), new Baby("sw", 1), new Baby("yr", 33));

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(babyFlux, Baby.class);
    }

    public Mono<ServerResponse> createBaby(ServerRequest request) {

        Mono<Baby> mono = request.bodyToMono(Baby.class);

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(mono, Baby.class);

    }

 }

@Service
class BabyService {

    public Mono<Baby> baby(String name) {

        Baby baby = Stream.of(new Baby("mj", 1), new Baby("sw", 1), new Baby("yr", 33))
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(new Baby("", 0));

        return Mono.just(baby);
    }

    public Flux<Baby> all() {
        return Flux.just(new Baby("mj", 1), new Baby("sw", 1), new Baby("yr", 33));
    }
}


@Configuration
class BabyRouter {

    @Bean
    RouterFunction<?> routes(BabyService bs, BabyHandler bh) {

        return RouterFunctions.route(GET("/baby").and(accept(APPLICATION_JSON)), bh::babyList)
                .andRoute(GET("/baby/{name}").and(accept(APPLICATION_JSON)), bh::baby)
                .andRoute(POST("/baby").and(accept(APPLICATION_JSON)), bh::createBaby)
                .andRoute(PUT("/baby"), serverRequest -> bh.createBaby(serverRequest));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Baby {
    private String name;
    private int age;
}

