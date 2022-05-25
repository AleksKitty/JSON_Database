package server;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ClientHandler {
    // lock mechanism
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    // imitation of Database
    private static final String FILENAME_ENVIRONMENT = System.getProperty("user.dir") + "/src/server/data/db.json";
    //Local:
//    private static final String FILENAME_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database/task/src/server/data/db.json";

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
        LinkedTreeMap<?, ?> map = gson.fromJson(new StringReader(command), LinkedTreeMap.class);

        if (map.get("type").equals("exit")) {
            this.exit();
        }

        return flagToExit;
    }

    protected void doCommand(String command) {

        LinkedTreeMap<?, ?> map = gson.fromJson(new StringReader(command), LinkedTreeMap.class);

        switch ((String) map.get("type")) {
            case "set":
                this.set(map);
                break;
            case "get":
                this.get((ArrayList<?>) map.get("key"));
                break;
            case "delete":
                this.delete((ArrayList<?>) map.get("key"));
                break;
            default:
                break;
        }
    }

    private void set(LinkedTreeMap<?, ?> data) {
        ArrayList<String> stringArray;
        if (data.get("key") instanceof String) {
            stringArray = new ArrayList<>();
            stringArray.add((String) data.get("key"));
        } else {
            stringArray = (ArrayList<String>) data.get("key");
        }
        // write to file
        writeToFileObject(stringArray, data.get("value"));

        // response
        ResponseToClient responseToClient = new ResponseToClient(Responses.OK.toString(), "", "");
        this.responseMsg = gson.toJson(responseToClient);
    }

    private void get(ArrayList<?> inputKey) {
        ResponseToClient responseToClient;

        Object value = readFromFileByKey(inputKey);
        if (value != null) {
            responseToClient = new ResponseToClient(Responses.OK.toString(), value, "");
        } else {
            responseToClient = new ResponseToClient(Responses.ERROR.toString(), "", NO_SUCH_KEY);
        }
        this.responseMsg = gson.toJson(responseToClient);
    }

    private void delete(ArrayList<?> inputKey) {

        ResponseToClient responseToClient;
        boolean result = removeFromFileObject(inputKey);

        if (result) {
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

    private void writeToFileObject(ArrayList<String> keys, Object data) {

        // get json
        JsonElement jsonWholeElement = null;
        readLock.lock();
        try (Reader reader = new FileReader(FILENAME_ENVIRONMENT)) {
            jsonWholeElement = JsonParser.parseReader(reader);

            // update
            JsonElement jsonElement = jsonWholeElement;

            for (int i = 0; i < keys.size(); i++) {
                if (jsonElement != null && !(jsonElement instanceof JsonNull)) {
                    if (i == keys.size() - 1) {
                        if (data instanceof String) {
                            jsonElement.getAsJsonObject().addProperty(keys.get(i), (String) data);
                        } else {
                            JsonObject newJsonObject = gson.toJsonTree(data).getAsJsonObject();
                            jsonElement.getAsJsonObject().add(keys.get(i), newJsonObject);
                        }
                    } else {
                        jsonElement = jsonElement.getAsJsonObject().get(keys.get(i));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        readLock.unlock();

        // write to file
        writeLock.lock();
        try (FileWriter writer = new FileWriter(FILENAME_ENVIRONMENT)) {
            gson.toJson(jsonWholeElement, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeLock.unlock();
    }

    private Object readFromFileByKey(ArrayList<?> keys) {
        Object value = null;

        readLock.lock();
        try (Reader reader = new FileReader(FILENAME_ENVIRONMENT)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            for (Object key : keys) {
                if (jsonElement != null && !(jsonElement instanceof JsonNull)) {
                    jsonElement = jsonElement.getAsJsonObject().get((String) key);
                }
            }

            value = jsonElement;

        } catch (IOException e) {
            e.printStackTrace();
        }
        readLock.unlock();

        return value;
    }

    private boolean removeFromFileObject(ArrayList<?> keys) {
        JsonElement jsonWholeElement = null;
        JsonElement jsonElement;
        JsonElement removed = null;
        readLock.lock();
        try (Reader reader = new FileReader(FILENAME_ENVIRONMENT)) {
            jsonWholeElement = JsonParser.parseReader(reader);
            jsonElement = jsonWholeElement;

            for (int i = 0; i < keys.size(); i++) {
                if (jsonElement != null && !(jsonElement instanceof JsonNull)) {
                    if (i == keys.size() - 1) {
                        removed = jsonElement.getAsJsonObject().remove((String) keys.get(keys.size() - 1));
                    } else {
                        jsonElement = jsonElement.getAsJsonObject().get((String) keys.get(i));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        readLock.unlock();

        if (removed != null) {
            writeLock.lock();
            try (FileWriter writer = new FileWriter(FILENAME_ENVIRONMENT)) {

                gson.toJson(jsonWholeElement, writer);
                writeLock.unlock();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                writeLock.unlock();
            }
        }
        return false;
    }
}
