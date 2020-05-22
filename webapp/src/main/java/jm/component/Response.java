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
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class Response<T> {

    private static boolean success;

    private final Object status;

    private final HttpHeaders headers;

    @Nullable
    private final T body;

    private Response(@Nullable T body, @Nullable MultiValueMap<String, String> headers, Object status) {
        this.body = body;
        HttpHeaders tempHeaders = new HttpHeaders();
        if (headers != null) {
            tempHeaders.putAll(headers);
        }
        this.headers = HttpHeaders.readOnlyHttpHeaders(tempHeaders);
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }


    public static BodyBuilder ok() {
        success = true;
        return httpStatus(HttpStatus.OK);
    }

    public static BodyBuilder ok(HttpStatus status) {
        success = true;
        return httpStatus(status);
    }

    public static <T> Response<T> ok(T body) {
        BodyBuilder builder = ok();
        return builder.ok(body);
    }

    public static <T> Response<T> ok(HttpStatus status, T body) {
        BodyBuilder builder = new DefaultBuilder(status);
        return builder.ok(body);
    }

    public static BodyBuilder error(HttpStatus status) {
        success = false;
        Assert.notNull(status, "HttpStatus must not be null");
        return httpStatus(status);
    }

    public static Response<String> error(HttpStatus status, String text) {
        success = false;
        Assert.notNull(status, "HttpStatus must not be null");
        BodyBuilder builder = httpStatus(status);
        return builder.message(text);
    }

    public static BodyBuilder error(int status) {
        success = false;
        return new DefaultBuilder(status);
    }

    private static BodyBuilder httpStatus(HttpStatus status) {
        Assert.notNull(status, "HttpStatus must not be null");
        return new DefaultBuilder(status);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.status.toString());
        if (this.status instanceof HttpStatus) {
            builder.append(' ');
            builder.append(((HttpStatus) this.status).getReasonPhrase());
        }
        builder.append(',');
        if (this.body != null) {
            builder.append(this.body);
            builder.append(',');
        }
        builder.append(this.headers);
        builder.append('>');
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return (29 * (ObjectUtils.nullSafeHashCode(this.headers) * 29 + ObjectUtils.nullSafeHashCode(this.body)) + ObjectUtils.nullSafeHashCode(this.status));
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Nullable
    public T getBody() {
        return this.body;
    }

    public boolean hasBody() {
        return (this.body != null);
    }

    public HttpStatus getStatusCode() {
        if (this.status instanceof HttpStatus) {
            return (HttpStatus) this.status;
        } else {
            return HttpStatus.valueOf((Integer) this.status);
        }

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

        <T> Response<T> build();

        B status(HttpStatus httpStatus);

    }

    //
    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {

        BodyBuilder contentType(MediaType var1);

        Response<String> message(String message);

        <T> Response<T> ok(T body);

    }

    private static class DefaultBuilder implements BodyBuilder {

        private final Object statusCode;

        private final HttpHeaders headers = new HttpHeaders();


        public DefaultBuilder(Object statusCode) {
            this.statusCode = statusCode;
        }


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
        public <T> Response<T> build() {
            return new Response<>(null, this.headers, this.statusCode);
        }

        @Override
        public <T> Response<T> ok(T body) {
            String text = "On error, only text";
            return (success) ? new Response<>(body, this.headers, this.statusCode)
                    : new Response<T>((T) text, this.headers, this.statusCode);
        }

        @Override
        public BodyBuilder status(HttpStatus httpStatus) {
            return error(httpStatus);
        }

        @Override
        public Response<String> message(String message) {
            return new Response<>(message, this.headers, this.statusCode);
        }

    }
}

