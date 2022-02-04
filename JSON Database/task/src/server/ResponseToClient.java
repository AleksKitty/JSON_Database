package server;

public class ResponseToClient {
    private final String response;
    private String value;
    private String reason;

    public ResponseToClient(String response, String value, String reason) {
        this.response = response;

        if (!value.isEmpty()) {
            this.value = value;
        }

        if (!reason.isEmpty()) {
            this.reason = reason;
        }
    }

    public String getResponse() {
        return response;
    }

    public String getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
