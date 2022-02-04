package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 56401;

    private static final Args clientArguments = new Args();
//    private static final Gson gson = new GsonBuilder()
//            .excludeFieldsWithoutExposeAnnotation()
//            .setPrettyPrinting()
//            .create();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        System.out.println("Client started!");

        // from args to clientArguments
        processMsg(args);
        // make message in gson
        String message = makeMessage();

        System.out.println("Sent: " + message);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(message); // sending message to the server

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        String receivedMsg = (String) input.readObject(); // response message
        System.out.println("Received: " + receivedMsg);

        socket.close();
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

        MessageFromClient messageFromClient = new MessageFromClient(clientArguments.getCommand(),
                clientArguments.getIndex(), clientArguments.getTextValue());

        return gson.toJson(messageFromClient);
    }
}
