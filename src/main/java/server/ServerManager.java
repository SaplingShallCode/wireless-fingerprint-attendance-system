package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import gui.MainWindow;


/**
 * The ServerManager class will listen for clients trying to connect.
 * These clients are be fingerprint scanners. The server is able to
 * handle multiple clients. The server will serve clients data from
 * the database and also update the database data.
 */
@SuppressWarnings("unused")
public class ServerManager implements Runnable {
    private final ServerSocket serversocket;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final MainWindow app;
    private boolean isRunning;



    /**
     * @param hostname the fixed host name.
     * @param port the fixed port number.
     * @exception IOException error when opening the socket.
     */
    public ServerManager(MainWindow app, String hostname, int port) throws IOException {
        this.app = app;
        serversocket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(hostname, port);
        serversocket.bind(address);
    }


    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                app.sendToConsole("Waiting for a connection on port " + serversocket.getLocalPort());
                // blocks current thread while waiting for a client to connect. will throw an IOException.
                Socket client = serversocket.accept();

                // create a thread for the connected client and run the thread.
                ClientHandler client_handler = new ClientHandler(client);
                clients.add(client_handler); // add the client to a list to access ClientHandler methods.
                new Thread(client_handler).start();
            }
            catch (IOException e) {
                try {
                    stopServer();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }


    /**
     * Close the server.
     */
    public void stopServer() throws IOException {
        isRunning = false;
        serversocket.close();
    }


    /**
     * Returns the state of the server.
     * @return true if the socket is closed.
     */
    public boolean isClosed() {
        return serversocket.isClosed();
    }


    /**
     * Returns the list of clients connected to the server.
     * @return the Arraylist of clients.
     */
    public ArrayList<ClientHandler> getClients() {
        return clients;
    }


    /**
     * Remove the client from the clients list upon disconnection.
     * @param c a client socket.
     */
    public void removeClient(ClientHandler c) {
        this.clients.remove(c);
    }


    /**
     * The ClientHandler is a nested class that will handle a client that
     * connected to the server in another thread.
     */
    private class ClientHandler extends Thread {
        private final Socket client;
        private BufferedReader input;
        private BufferedWriter output;
        private boolean isConnected;


        /**
         * @param socket the client socket that connected to the server.
         */
        public ClientHandler(Socket socket) {
            this.client = socket;
        }


        /**
         * Set the Input and Output streams of the client.
         *
         * @throws IOException error when creating input and output streams.
         */
        private void setIO() throws IOException {
            input = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            output = new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())
            );
        }


        /**
         * Disconnect the client from the server.
         */
        private void disconnect() {
            isConnected = false;
        }


        @Override
        public void run() {
            isConnected = true;
            try {
                app.sendToConsole("Just connected to client: " + client.getRemoteSocketAddress());
                // connect input and output streams for communication and send feedback to the client
                setIO();
                // TODO: remove next three output lines.
                output.write("[server] You are connected to " + client.getLocalSocketAddress());
                output.newLine();
                output.flush();

                /* TODO: create a main loop. the main loop will listen for data from the client.*/
                String message;
                while (isConnected) {
                    message = input.readLine();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                removeClient(this);
                app.sendToConsole("Closing connection for " + client.getRemoteSocketAddress());
                try {
                    client.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}