package server;


import client.MessageFromClient;
import com.google.gson.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ClientHandler {
    // lock mechanism
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    // imitation of Database
    private final Gson gsonForFile = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILENAME_TEST_ENVIRONMENT = System.getProperty("user.dir") + "/src/server/data/db.json";
    private static final String FILENAME_LOCAL_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database/task/src/server/data/db.json";

    // to work with json
    private final Gson gson = new Gson();

    // true - exit from user interface
    private boolean flagToExit = false;

    private String responseMsg;

    // Types of responses to the sever
    private enum Responses {OK, ERROR}

    private static final String NO_SUCH_KEY = "No such key";

    public String getResponseMsg() {
        return responseMsg;
    }

    protected boolean checkForExit(String command) {
        MessageFromClient msgCommand = gson.fromJson(command, MessageFromClient.class);

        if (msgCommand.getType().equals("exit")) {
            this.exit();
        }

        return flagToExit;
    }

    protected void doCommand(String command) {
        
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
            default:
                break;
        }
    }

    private void set(MessageFromClient msgCommand) {
        // write to file
        HashMap<String, String> outputMap = new HashMap<>();
        outputMap.put(msgCommand.getKey(), msgCommand.getValue());
        writeToFile(outputMap);

        // response
        ResponseToClient responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        this.responseMsg = gson.toJson(responseToClient);
    }

    private void get(String inputKey) {

        ResponseToClient responseToClient;
        if (checkIfNullOrEmpty(inputKey)) {
            String value = readFromFileByKey(inputKey);
            responseToClient = new ResponseToClient(Responses.OK.toString(), value, "");
        } else {
            responseToClient = new ResponseToClient(Responses.ERROR.toString(), "", NO_SUCH_KEY);
        }
        this.responseMsg = gson.toJson(responseToClient);
    }

    private void delete(String inputKey) {

        ResponseToClient responseToClient;

        if (checkIfNullOrEmpty(inputKey)) {
            // write to file
            HashMap<String, String> outputMap = new HashMap<>();
            outputMap.put(inputKey, "");
            writeToFile(outputMap);

            responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        } else {
            responseToClient = new ResponseToClient(Responses.ERROR.toString(), "", NO_SUCH_KEY);
        }

        this.responseMsg = gson.toJson(responseToClient);
    }

    private void exit() {
        flagToExit = true;

        ResponseToClient responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        this.responseMsg = gson.toJson(responseToClient);
    }

    private boolean checkIfNullOrEmpty(String inputKey) {
        String value = readFromFileByKey(inputKey);

        return value != null && !value.isEmpty();
    }

    private void writeToFile(HashMap<String, String> data) {
        // write to file
        writeLock.lock();
        try (FileWriter writer = new FileWriter(FILENAME_TEST_ENVIRONMENT)) {
            gsonForFile.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeLock.unlock();
    }

    private String readFromFileByKey(String key) {
        String value = null;

        try {
            Reader reader = new FileReader(FILENAME_TEST_ENVIRONMENT);

            // Convert JSON File to Java Object
            readLock.lock();
            Map<?, ?> map = gsonForFile.fromJson(reader, Map.class);
            readLock.unlock();

            value = (String) map.get(key);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }
}
