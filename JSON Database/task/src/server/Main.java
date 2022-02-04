package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final int PORT = 56401;
    private static final String ADDRESS = "127.0.0.1";
    private static boolean flagToExit = false;

    public static void main(String[] arguments) {

        User user = new User();

        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            while (!flagToExit) {
                try (
                        Socket socket = server.accept(); // accepting a new client
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ) {
                    String command = (String) input.readObject(); // reading a message
                    System.out.println("Received: " + command);

                    flagToExit = user.inputCommand(command);

                    System.out.println("Sent: " + user.getResponseMsg());
                    output.writeObject(user.getResponseMsg()); // send it to the client
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
