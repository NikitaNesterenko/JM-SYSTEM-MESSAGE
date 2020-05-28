package jm.component;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;


public class Response<T> extends HttpEntity<T> {

    private static boolean success;

    private final Object status;


    private Response(@Nullable T body, @Nullable MultiValueMap<String, String> headers, Object status) {

        super(body, headers);
        this.status = status;
    }
    public static BodyBuilder ok() {
        success = true;
        return httpStatus(HttpStatus.OK);
    }

    public static BodyBuilder ok(HttpStatus status) {
        success = false;
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
        return httpStatus(status);
    }
/*
    public static Response<String> error(HttpStatus status, String text) {
        success = false;
        BodyBuilder builder = httpStatus(status);
        return builder.message(text);
    }*/

    public static <T> Response<T> error(HttpStatus status, String text) {
        success = false;
        BodyBuilder builder = httpStatus(status);
        return (Response<T>) builder.message(text);
    }

    private static BodyBuilder httpStatus(HttpStatus status) {
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

        T body = getBody();
        HttpHeaders headers = getHeaders();
        if (body != null) {
            builder.append(body);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }


    @Override
    public int hashCode() {
        return (29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.status));
    }


    public HttpStatus getStatusCode() {
        if (this.status instanceof HttpStatus) {
            return (HttpStatus) this.status;
        } else {
            return HttpStatus.valueOf((Integer) this.status);
        }
    }

    public interface BodyBuilder {

        BodyBuilder contentType(MediaType var1);

        Response<String> message(String message);

        <T> Response<T> ok(T body);

        <T> Response<T> build();

        BodyBuilder status(HttpStatus httpStatus);

        BodyBuilder headers(@Nullable HttpHeaders headers);

        BodyBuilder header(String headerName, String... headerValues);

    }

    private static class DefaultBuilder implements BodyBuilder {

        private final Object statusCode;

        private final HttpHeaders headers = new HttpHeaders();

        private DefaultBuilder(Object statusCode) {
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
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
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