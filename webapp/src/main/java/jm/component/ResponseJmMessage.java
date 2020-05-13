package jm.component;

/**
 * нужно написать аналог Response
 * с полем Boolean success
 * Сущность можно сконструировать по аналогии с ResponseEntity<>
 * Она параметризована
 * + если есть ошибка, то нельзя положить в тело какую либо сущность, можно лишь сообщение
 * Response.ok(T body).build();
 * Response.error().status(либо статический статус, либо сам код).message( текст).build();
 */

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class ResponseJmMessage<T> extends HttpEntity<T> {

    private static boolean success;

    private static Object status;

    private ResponseJmMessage(HttpStatus status) {
        this(null, null, status);
    }

    //
    private ResponseJmMessage(@Nullable T body, HttpStatus status) {
        this(body, null, status);
    }

    //
    private ResponseJmMessage(MultiValueMap<String, String> headers, HttpStatus status) {
        this(null, headers, status);
    }

    private ResponseJmMessage(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers);
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }

    public static BodyBuilder ok() {
        success = true;
        return httpStatus(HttpStatus.OK);
    }


    public static <T> ResponseJmMessage<T> ok(@Nullable T body) {
        BodyBuilder builder = ok();
        return builder.body(body);
    }


    public static BodyBuilder error(HttpStatus status) {
        success = false;
        Assert.notNull(status, "HttpStatus must not be null");
        return httpStatus(status);
    }

//    public static BodyBuilder error(int status) {
//        success = false;
//        return new DefaultBuilder(status);
//    }

//    public static BodyBuilder error() {
//        success = false;
//        return new DefaultBuilder();
//    }

    private static BodyBuilder httpStatus(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        return new DefaultBuilder(status);
    }


    public interface HeadersBuilder<B extends HeadersBuilder<B>> {

        B header(String headerName, String... headerValues);

        B headers(@Nullable HttpHeaders headers);

        B allow(HttpMethod... allowedMethods);

        B eTag(String etag);

        B lastModified(ZonedDateTime lastModified);

        B lastModified(Instant lastModified);

        B lastModified(long lastModified);

        B location(URI location);

        B cacheControl(CacheControl cacheControl);

        B varyBy(String... requestHeaders);

        <T> ResponseJmMessage<T> build();

        B status(HttpStatus httpStatus);

//        B message(String message);

    }

    //
    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {

        BodyBuilder contentLength(long var1);

        BodyBuilder contentType(MediaType var1);

        <T> ResponseJmMessage<T> body(@Nullable T var1);
    }

    //
    private static class DefaultBuilder implements BodyBuilder {

        private final HttpStatus httpStatus;

        private final HttpHeaders headers = new HttpHeaders();

//        public DefaultBuilder() {
//
//        }
//
//        public DefaultBuilder(String string) {
//            this.string = string;
//        }

        public DefaultBuilder(HttpStatus httpStatus) {

            this.httpStatus = httpStatus;
        }

//        public DefaultBuilder(int statusCode) {
//            this.httpStatus = HttpStatus.valueOf(statusCode);
//        }


        @Override
        public BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        @Override
        public BodyBuilder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override
        public BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        @Override
        public BodyBuilder eTag(String etag) {
            if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                etag = "\"" + etag;
            }
            if (!etag.endsWith("\"")) {
                etag = etag + "\"";
            }
            this.headers.setETag(etag);
            return this;
        }

        @Override
        public BodyBuilder lastModified(ZonedDateTime date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder lastModified(Instant date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        @Override
        public BodyBuilder cacheControl(CacheControl cacheControl) {
            this.headers.setCacheControl(cacheControl);
            return this;
        }

        @Override
        public BodyBuilder varyBy(String... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override
        public <T> ResponseJmMessage<T> build() {
            return body(null);
        }

        @Override
        public <T> ResponseJmMessage<T> body(@Nullable T body) {
            return (success) ? new ResponseJmMessage<>(body, this.headers, this.httpStatus)
                    : new ResponseJmMessage<>(null, this.headers, this.httpStatus);
        }


        @Override
        public BodyBuilder status(HttpStatus httpStatus) {
            return error(httpStatus);
        }

//        @Override
//        public BodyBuilder message(String message) {
//            return new DefaultBuilder(message);
//        }
    }
}

