package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT = 56401;
    private static final String ADDRESS = "127.0.0.1";
    private static boolean flagToExit = false;

    public static void main(String[] arguments) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
            System.out.println("Server started!");
            ExecutorService executor = Executors.newFixedThreadPool(4);

            while (!flagToExit) {
                try {
                    Socket socket = serverSocket.accept(); // accepting a new client
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                    String command = (String) input.readObject(); // reading a message
                    System.out.println("Received: " + command);

                    executor.submit(() -> {

                        ClientHandler clientHandler = new ClientHandler();
                        flagToExit = clientHandler.doCommand(command);

                        try {
                            output.writeObject(clientHandler.getResponseMsg()); // send it to the client
                            System.out.println("Sent: " + clientHandler.getResponseMsg());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        if (flagToExit) {
                            try {
                                serverSocket.close();
                                System.exit(0);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    });

                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
