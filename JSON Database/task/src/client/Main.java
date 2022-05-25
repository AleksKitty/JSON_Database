package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.net.Socket;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 56401;

    private static final String FILENAME_ENVIRONMENT = System.getProperty("user.dir") + "/src/client/data/";
    // Local:
//    private static final String FILENAME_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database/task/src/client/data/";

    private static final Args clientArguments = new Args();

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Client started!");

            // from args to clientArguments
            processMsg(args);
            // make message in gson
            String message = makeMessage();

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(message); // sending message to the server
            System.out.println("Sent: " + message);

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            String receivedMsg = (String) input.readObject(); // response message
            System.out.println("Received: " + receivedMsg);

            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void processMsg(String[] args) {
        // input arguments properly
        JCommander.newBuilder()
                .addObject(clientArguments)
                .build()
                .parse(args);
    }

    private static String makeMessage() {
        Gson gson = new Gson();

        MessageFromClient messageFromClient;

        LinkedTreeMap<?, ?> map = null;
        if (clientArguments.getInputFile() != null) {
            try {
                Reader reader = new FileReader(FILENAME_ENVIRONMENT + clientArguments.getInputFile());

                // Convert JSON File to Java Object
                map = gson.fromJson(reader, LinkedTreeMap.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            messageFromClient = new MessageFromClient(clientArguments.getCommand(),
                    clientArguments.getKey(), clientArguments.getTextValue());
            return gson.toJson(messageFromClient);
        }

        return gson.toJson(map);
    }
}
