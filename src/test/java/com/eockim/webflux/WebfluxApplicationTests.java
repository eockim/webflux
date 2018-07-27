package com.eockim.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WebfluxApplicationTests {

    @Test
    public void contextLoads() {
        Flux<String> f = Flux.fromIterable(Arrays.asList("a", "b", "c", "d"))
                .delayElements(Duration.ofMillis(100))
                .doOnNext(s -> s.concat("-"))
                .map(s -> s + ":")
                .take(3);


        Flux.fromIterable(Arrays.asList("foo", "bar"));

        Flux.error(new IllegalStateException());
    }

    @Test
    public void exception(){
        Flux.error(new IllegalStateException());
    }

    @Test
    public void generator(){

        AtomicReference<Long> l = new AtomicReference<>(0L);
        Flux<Long> result = Flux.generate((SynchronousSink<Long> sync) -> {
            sync.next(l.getAndSet(l.get() + 1));
        }).delayElements(Duration.ofMillis(100))
                .take(10);
        Flux.range(0, 10)
                .delayElements(Duration.ofMillis(100));

        Flux.generate(() -> 0L, (state, sink) -> {
            sink.next(state++);
            if (state == 10l) {
                sink.complete();
            }
            return state;
        });

    }

    @Test
    public void mono(){
        Mono.empty().log();

        Mono.never();
        Mono.error(new Exception());

        Mono<String> m = Mono.just("test string");
        Flux<String> f = Flux.just("toy", "tv", "computer");
        Flux<String> f2 = Flux.just("toy", "tv", "computer");

        f.mergeWith(f2);

        Mono.just("test")
                .subscribe(System.out::println);

    }

    @Test
    public void flux(){

        Flux<Integer> f3 = Flux.just("1", "2")
                .flatMap(s -> Mono.just(Integer.parseInt(s)));

        Flux<Integer> f4 = Flux.just("1", "2")
                .map(s -> Integer.parseInt(s));

        Flux<String> f1 = Flux.just("a", "b", "d", "c", "f")
                .log()
                .map(s -> "[" + s + "]");

        Flux<String> f2 = Flux.just("a", "b", "d", "c", "f")
                .flatMap(s -> Mono.just("[" + s + "]"));

        f1.log()
        .subscribe(System.out::println);

        f2.subscribe(System.out::println);

    }

    @Test
    public void flux2() throws InterruptedException {

        Flux.just("a", "b", "d", "c", "f")
                .log()
                .flatMap(s-> {

                    return Flux.just("[" + s + "]", "3");
                })
                .delayElements(Duration.ofMillis(5 * 1000L))
                .subscribe(System.out::println);

        Flux.just("a", "b", "d", "c", "f")
                .log()
                .map(s-> {
//                    if(s.equals("d")){
//                        try {
//                            Thread.sleep(10 * 1000L);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    return "#" + s + "#";
                })
                .subscribe(System.out::println);

        Thread.sleep(100 * 1000L);
    }

    @Test
    public void fluxConcat() throws InterruptedException {

        Flux<String> flux1 = Flux.just("[1]", "[2]", "[3]", "[4]");
        Flux<String> flux2 = Flux.just("(1)", "(2)", "(3)", "(4)", "(5)");

        Flux<String> intervalF1 = Flux.interval(Duration.ofMillis(500L))
                .zipWith(flux1, (i, str) -> str);

        Flux<String> intervalF2 = Flux.interval(Duration.ofMillis(700L))
                .zipWith(flux2, (i, str) -> str);

        Flux.concat(flux1, flux2)
                //.log()
                .subscribe(s -> System.out.print(s));

        Thread.sleep(3 * 1000L);
        System.out.println();
        flux1.concatWith(flux2)
                .subscribe(System.out::print);

        System.out.println();
        Thread.sleep(3* 1000L);
        Flux.concat(intervalF2, flux1).subscribe(System.out::print);

        System.out.println();
        Thread.sleep(6* 1000L);

    }

    @Test
    public void fluxMerge() throws InterruptedException {

        Flux<String> flux1 = Flux.just("[1]", "[2]", "[3]", "[4]");
        Flux<String> flux2 = Flux.just("(1)", "(2)", "(3)", "(4)", "(5)");

        Flux<String> intervalF1 = Flux.interval(Duration.ofMillis(500L))
                .zipWith(flux1, (i, str) -> str);

        Flux<String> intervalF2 = Flux.interval(Duration.ofMillis(700L))
                .zipWith(flux2, (i, str) -> str);

        Flux.merge(intervalF1, intervalF2)
                .subscribe(System.out::print);

        Thread.sleep(3* 1000L);


    }

    @Test
    public void fluxZip() throws InterruptedException {

        Flux<String> flux1 = Flux.just("[1]", "[2]", "[3]", "[4]");
        Flux<String> flux2 = Flux.just("(1)", "(2)", "(3)", "(4)", "(5)");


        Flux.zip(flux1, flux2, (item1, item2) -> "#" + item1 + "//" + item2 + "#")
                .subscribe(System.out::print);


    }

    @Test
    public void errorHandle(){

        Flux<String> f = Flux.range(1, 4)
                .map(s -> {
                    if (s <= 3) {
                        return "item : " + s;
                    } else {
                        System.out.println("<< throw RuntimeExcpetion.");
                        throw new RuntimeException();
                    }
                });

//        f.doOnError(e -> {
//            e.printStackTrace();
//            System.out.println("doOnError : " + e);
//        })
//                .subscribe(System.out::println);
//
//        f.onErrorReturn("에러 : " + 3)
//                .subscribe(System.out::println);
//
//        f.onErrorResume(e -> Flux.just("error"))
//                .subscribe(System.out::println);

//        f.retry(2L)
//                .subscribe(System.out::println);

            f.retry(1, e ->{
                return false;
            }).subscribe(System.out::println);
    }

    @Test
    public void pubSub(){

        Flux.just("a", "b", "c", "d")
                .map(s -> "$" + s)
                .map(s -> {
                    log.info("before publish on");
                    return s + "!";
                })
                .publishOn(Schedulers.elastic())
                .map(s -> {
                    log.info("after publish on");
                    return s.toUpperCase();
                })
                .log()
                .subscribe(System.out::println);
    }

    @Test
    public void pubSub2(){

        Flux.just("a", "b", "c", "d")
                .map(s -> "$" + s)
                .map(s -> {
                    log.info("before publish on");
                    return s + "!";
                })
                .subscribeOn(Schedulers.parallel())
                .map(s -> {
                    log.info("after publish on");
                    return s.toUpperCase();
                })
                .log()
                .subscribe(System.out::println);
    }

}
