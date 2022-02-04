package server;


import client.MessageFromClient;
import com.google.gson.Gson;

import java.util.HashMap;

public class User {

    // imitation of Database
    private final HashMap<String, String> cellDatabse = new HashMap<>();

    // true - exit from user interface
    private boolean flagToExit = false;

    private String responseMsg;

    // Types of responses to the sever
    private enum Responses {OK, ERROR}

    private static final String NO_SUCH_KEY = "No such key";

    public String getResponseMsg() {
        return responseMsg;
    }

    protected boolean inputCommand(String command) {

        Gson gson = new Gson();
        MessageFromClient msgCommand = gson.fromJson(command, MessageFromClient.class);

        switch (msgCommand.getType()) {
            case "set":
                this.set(msgCommand);
                break;
            case "get":
                this.get(msgCommand.getKey());
                break;
            case "delete":
                this.delete(msgCommand.getKey());
                break;
            case "exit":
                this.exit();
                break;
            default:
                break;
        }

        return flagToExit;
    }

    private void set(MessageFromClient msgCommand) {
        this.cellDatabse.put(msgCommand.getKey(), msgCommand.getValue());

        Gson gson = new Gson();
        ResponseToClient responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        this.responseMsg = gson.toJson(responseToClient);
    }

    private void get(String inputKey) {

        Gson gson = new Gson();
        ResponseToClient responseToClient;
        if (checkIfNullOrEmpty(inputKey)) {
            responseToClient = new ResponseToClient(Responses.OK.toString(), this.cellDatabse.get(inputKey), "");
        } else {
            responseToClient = new ResponseToClient(Responses.ERROR.toString(), "", NO_SUCH_KEY);
        }
        this.responseMsg = gson.toJson(responseToClient);
    }

    private void delete(String inputKey) {

        Gson gson = new Gson();
        ResponseToClient responseToClient;

        if (checkIfNullOrEmpty(inputKey)) {
            this.cellDatabse.put(inputKey, "");
            responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        } else {
            responseToClient = new ResponseToClient(Responses.ERROR.toString(), "", NO_SUCH_KEY);
        }

        this.responseMsg = gson.toJson(responseToClient);
    }

    private void exit() {
        flagToExit = true;

        Gson gson = new Gson();
        ResponseToClient responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        this.responseMsg = gson.toJson(responseToClient);
    }

    private boolean checkIfNullOrEmpty(String inputKey) {
        return this.cellDatabse.get(inputKey) != null && !this.cellDatabse.get(inputKey).isEmpty();
    }
}
