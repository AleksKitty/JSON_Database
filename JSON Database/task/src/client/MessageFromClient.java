package client;

public class MessageFromClient {
    private final String type;
    private String key;
    private String value;

    public MessageFromClient(String type, String key, String value) {
        this.type = type;

        if (key != null && !key.isEmpty()) {
            this.key = key;
        }

        if (value != null && !value.isEmpty()) {
            this.value = value;
        }
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
