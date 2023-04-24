package server;

import java.io.*;
import java.net.Socket;

/* TODO: implement ClientHandler class */

/**
 * The ClientHandler class will handle the multiple clients connected to the
 * server. The ClientHandler will keep listening for the client's request and
 * will provide data for the clients. The ClientHandler is part of the server.
 */
public class ClientHandler implements Runnable {
    private final Socket client;

    /**
     * @param socket the client socket that connected to the server.
     */
    public ClientHandler(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("[info] just connected to client: " + client.getRemoteSocketAddress());
            // connect input and output streams for communication and send feedback to the client
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            BufferedWriter output = new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())
            );
            output.write("[server] You are connected to " + client.getLocalSocketAddress());
            output.newLine();
            output.flush();

            /* TODO: create a main loop. the main loop will listen for data from the client.*/
            while (true) {
                break;
            }

            System.out.println("[info] closing connection for " + client.getRemoteSocketAddress());
            client.close();
        }
        catch (IOException e) {
            // will occur when failing to connect input and output streams.
            e.printStackTrace();
        }
    }
}
