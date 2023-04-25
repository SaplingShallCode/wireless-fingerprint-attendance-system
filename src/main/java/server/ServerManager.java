package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


/**
 * The ServerManager class will listen for clients trying to connect.
 * These clients are be fingerprint scanners. The server is able to
 * handle multiple clients. The server will serve clients data from
 * the database and also update the database data.
 */
public class ServerManager implements Runnable{
    private ServerSocket serversocket;
    private boolean isRunning;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();


    /**
     * @param hostname the fixed host name.
     * @param port the fixed port number.
     * @exception IOException error when opening the socket.
     */
    public ServerManager(String hostname, int port) throws IOException{
        serversocket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(hostname, port);
        serversocket.bind(address);
    }

    @Override
    public void run() {
        runServer();
    }


    /**
     * Run the server.
     */
    public void runServer() {
        isRunning = true;
        while (isRunning) {
            try {
                System.out.println("[info] Waiting for a connection on port " + serversocket.getLocalPort());
                // blocks current thread while waiting for a client to connect. will throw an IOException.
                Socket client = serversocket.accept();

                /* TODO: create a table in the database for fingerprint scanner clients. will be used for
                 *   client verification. */

                // create a thread for the connected client and run the thread.
                ClientHandler client_handler = new ClientHandler(client);
                clients.add(client_handler); // add the client to a list to access ClientHandler methods.
                new Thread(client_handler).start();
            }
            catch (IOException e) {
                stopServer();
            }
        }
    }


    /**
     * Close the server.
     */
    public void stopServer() {
        isRunning = false;
        try {
            serversocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        public void disconnect() {
            isConnected = false;
        }


        @Override
        public void run() {
            isConnected = true;
            try {
                System.out.println("[info] just connected to client: " + client.getRemoteSocketAddress());
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
                System.out.println("[info] closing connection for " + client.getRemoteSocketAddress());
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
