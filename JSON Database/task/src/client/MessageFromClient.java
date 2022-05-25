package client;

public class MessageFromClient {
    private final String type;
    private String key;
    private String value;

    public MessageFromClient(String type, String key, String value) {
        this.type = type;

        if (key != null) {
            this.key = key;
        }

        if (value != null && !value.isEmpty()) {
            this.value = value;
        }
    }
}
