package packagee.response;

public class Response<T> {

    private final StatusCode statusCode;
    private final String message;
    private final T data;

    public Response(StatusCode statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(StatusCode.OK, message, data);
    }

    public static <T> Response<T> created(String message, T data) {
        return new Response<>(StatusCode.CREATED, message, data);
    }

    public static <T> Response<T> badRequest(String message) {
        return new Response<>(StatusCode.BAD_REQUEST, message, null);
    }

    public static <T> Response<T> unauthorized(String message) {
        return new Response<>(StatusCode.UNAUTHORIZED, message, null);
    }

    public static <T> Response<T> forbidden(String message) {
        return new Response<>(StatusCode.FORBIDDEN, message, null);
    }

    public static <T> Response<T> notFound(String message) {
        return new Response<>(StatusCode.NOT_FOUND, message, null);
    }

    public static <T> Response<T> conflict(String message) {
        return new Response<>(StatusCode.CONFLICT, message, null);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(StatusCode.ERROR, message, null);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return statusCode == StatusCode.OK || statusCode == StatusCode.CREATED;
    }
}

