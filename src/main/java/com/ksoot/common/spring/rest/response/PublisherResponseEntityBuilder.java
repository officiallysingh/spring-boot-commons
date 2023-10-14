package com.ksoot.common.spring.rest.response;

import com.ksoot.common.spring.rest.response.builder.AbstractResponseBuilder;
import com.ksoot.common.spring.rest.response.builder.HeaderBuilder;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Rajveer Singh
 */
public class PublisherResponseEntityBuilder {

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> of(
      final Optional<T> body) {
    Assert.notNull(body, "Body must not be null");
    return of(Mono.justOrEmpty(body));
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> of(final Mono<T> body) {
    Assert.notNull(body, "Body must not be null");
    return body.hasElement()
        .map(notEmpty -> new MonoResponseEntityBuilder<T>(body, HttpStatus.OK))
        .defaultIfEmpty(new MonoResponseEntityBuilder<>(HttpStatus.NOT_FOUND))
        .block();
  }

  public static <T> HeaderBuilder<Flux<T>, Mono<ResponseEntity<Flux<T>>>> of(final Flux<T> body) {
    Assert.notNull(body, "Body must not be null");
    return body.hasElements()
        .map(notEmpty -> new FluxResponseEntityBuilder<T>(body, HttpStatus.OK))
        .defaultIfEmpty(new FluxResponseEntityBuilder<>(HttpStatus.NOT_FOUND))
        .block();
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> accepted() {
    return new MonoResponseEntityBuilder<>(HttpStatus.ACCEPTED);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> badRequest() {
    return new MonoResponseEntityBuilder<>(HttpStatus.BAD_REQUEST);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> noContent() {
    return new MonoResponseEntityBuilder<>(HttpStatus.NO_CONTENT);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> notFound() {
    return new MonoResponseEntityBuilder<>(HttpStatus.NOT_FOUND);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> created(
      final URI location) {
    return new MonoResponseEntityBuilder<>(location);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> created(
      final URI location, final Mono<T> body) {
    return new MonoResponseEntityBuilder<>(location, body);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> ok(final T body) {
    return new MonoResponseEntityBuilder<>(Mono.just(body), HttpStatus.OK);
  }

  public static <T> HeaderBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> ok() {
    return new MonoResponseEntityBuilder<>(HttpStatus.OK);
  }

  public static class MonoResponseEntityBuilder<T>
      extends AbstractResponseBuilder<Mono<T>, Mono<ResponseEntity<Mono<T>>>> {

    private URI location;

    MonoResponseEntityBuilder(final HttpStatus status) {
      super(status);
    }

    MonoResponseEntityBuilder(final Mono<T> body) {
      super(body);
    }

    MonoResponseEntityBuilder(final Mono<T> body, final HttpStatus status) {
      super(body, status);
    }

    MonoResponseEntityBuilder(final URI location) {
      super(HttpStatus.CREATED);
      Assert.notNull(location, "location must not be null");
      this.location = location;
    }

    MonoResponseEntityBuilder(final URI location, final Mono<T> body) {
      super(body, HttpStatus.CREATED);
      Assert.notNull(location, "location must not be null");
      Assert.notNull(body, "body must not be null");
      this.location = location;
    }

    @Override
    public Mono<ResponseEntity<Mono<T>>> build() {
      org.springframework.http.ResponseEntity.BodyBuilder bodyBuilder =
          ResponseEntity.status(this.status);
      if (this.location != null) {
        bodyBuilder.location(this.location);
      }
      if (this.headers != null) {
        bodyBuilder.headers(this.headers);
      }
      return this.body != null
          ? Mono.just(bodyBuilder.body(this.body))
          : Mono.just(bodyBuilder.build());
    }
  }

  public static class FluxResponseEntityBuilder<T>
      extends AbstractResponseBuilder<Flux<T>, Mono<ResponseEntity<Flux<T>>>> {

    private URI location;

    FluxResponseEntityBuilder(final HttpStatus status) {
      super(status);
    }

    FluxResponseEntityBuilder(final Flux<T> body) {
      super(body);
    }

    FluxResponseEntityBuilder(final Flux<T> body, final HttpStatus status) {
      super(body, status);
    }

    FluxResponseEntityBuilder(final URI location) {
      super(HttpStatus.CREATED);
      Assert.notNull(location, "location must not be null");
      this.location = location;
    }

    @Override
    public Mono<ResponseEntity<Flux<T>>> build() {
      org.springframework.http.ResponseEntity.BodyBuilder bodyBuilder =
          ResponseEntity.status(this.status);
      if (this.location != null) {
        bodyBuilder.location(this.location);
      }
      if (this.headers != null) {
        bodyBuilder.headers(this.headers);
      }
      return this.body != null
          ? Mono.just(bodyBuilder.body(this.body))
          : Mono.just(bodyBuilder.build());
    }
  }

  private PublisherResponseEntityBuilder() {
    throw new IllegalStateException("Just a utility class, not supposed to be instantiated");
  }
}
