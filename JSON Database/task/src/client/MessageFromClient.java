package client;

public class MessageFromClient {
    private final String type;
    private String key;
    private String value;

    public MessageFromClient(String firstName, String keyString, String value) {
        this.type = firstName;

        if (!keyString.isEmpty()) {
            this.key = keyString;
        }

        if (!value.isEmpty()) {
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
