package server;

public class ResponseToClient {
    private final String response;
    private Object value;
    private String reason;

    public ResponseToClient(String response, Object value, String reason) {
        this.response = response;

        if (value instanceof String && ((String) value).isEmpty()) {
            this.value = null;
        } else if (value != null) {
            this.value = value;
        }

        if (!reason.isEmpty()) {
            this.reason = reason;
        }
    }
}
