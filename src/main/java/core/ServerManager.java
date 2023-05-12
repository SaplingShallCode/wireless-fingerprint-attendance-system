package core;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import gui.MainWindow;
import utility.LogHelper;
import utility.LogTypes;


/**
 * The ServerManager class will listen for clients trying to connect.
 * These clients are be fingerprint scanners. The server is able to
 * handle multiple clients. The server will serve clients data from
 * the database and also update the database data.
 */
@SuppressWarnings("unused")
public class ServerManager implements Runnable {
    private final ServerSocket server_socket;
    private final ArrayList<FSClient> fsclients = new ArrayList<>();
    private final MainWindow app;
    private boolean is_running;


    /**
     * @param hostname the fixed host name.
     * @param port the fixed port number.
     * @exception IOException error when opening the socket.
     */
    public ServerManager(MainWindow mw, String hostname, int port) throws IOException, IllegalArgumentException {
        app = mw;
        server_socket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(hostname, port);
        server_socket.bind(address);
    }


    @Override
    public void run() {
        is_running = true;
        while (is_running) {
            try {
                LogHelper.debugLog("Server started.");
                app.sendToConsole(LogHelper.log("Server started.", LogTypes.INFO));
                app.sendToConsole(LogHelper.log(
                        "Waiting for a connection on port " + server_socket.getLocalPort(),
                        LogTypes.SERVER
                        ));
                // blocks current thread while waiting for a client to connect. will throw an IOException.
                Socket client_socket = server_socket.accept();

                // create a thread for the connected client and run the thread.
                FSClient client = new FSClient(client_socket);
                // add to clients list for method access.
                fsclients.add(client);
                new Thread(client).start();
            }
            catch (IOException e) {
                LogHelper.debugLog("Closing all client sockets.");
                app.sendToConsole(LogHelper.log("Closing all client sockets.", LogTypes.INFO));
                for (FSClient client : fsclients) {
                    if (client != null) {
                        app.sendToConsole(LogHelper.log(
                                "Closing connection for " + client.getClientSocketAddress(),
                                LogTypes.INFO
                        ));
                        client.disconnect();
                        LogHelper.debugLog("client state: " + client.is_connected);
                    }
                }
            }
            finally {
                sendClientListUpdate();
                app.sendToConsole(LogHelper.log("All client sockets have been closed.", LogTypes.INFO));
                app.sendToConsole(LogHelper.log("Server sucessfully closed.", LogTypes.INFO));
                LogHelper.debugLog("Server stopped.");
            }
        }
    }


    /**
     * Close the server.
     * @throws IOException if an error occurs when closing the server.
     */
    public void stopServer() throws IOException {
        if (!is_running) {
            app.sendToConsole(LogHelper.log("Server is currently not running.", LogTypes.ERROR));
            throw new IOException();
        }
        is_running = false;
        app.sendToConsole(LogHelper.log("Closing server.", LogTypes.INFO));
        if (server_socket != null) {
            server_socket.close();
        }
    }


    /**
     * Returns the state of the server.
     * @return true if the socket is closed.
     */
    public boolean isClosed() {
        return server_socket.isClosed();
    }


    /**
     * Returns the list of clients connected to the server.
     * @return the Arraylist of clients.
     */
    public ArrayList<FSClient> getClients() {
        return fsclients;
    }


    /**
     * Updates the clients list from the gui.
     */
    public void sendClientListUpdate() {
        app.updateClientsList(fsclients);
    }


    /**
     * Remove the client from the clients list upon disconnection.
     * @param client a client socket.
     */
    public void removeClient(FSClient client) {
        fsclients.remove(client);
    }


    /**
     * The FSClient represents a Fingerprint Scanner Client. Every client object
     * runs in a new thread created by the server.
     */
    public class FSClient extends Thread {
        private final Socket client_socket;
        private BufferedReader input;
        private BufferedWriter output;
        private boolean is_connected;
        private final String client_socket_address;


        /**
         * @param socket the client socket that connected to the server.
         */
        public FSClient(Socket socket) {
            client_socket = socket;
            client_socket_address = client_socket.getRemoteSocketAddress().toString();
        }


        /**
         * Set the Input and Output streams of the client.
         *
         * @throws IOException error when creating input and output streams.
         */
        private void setIO() throws IOException {
            input = new BufferedReader(
                    new InputStreamReader(client_socket.getInputStream())
            );
            output = new BufferedWriter(
                    new OutputStreamWriter(client_socket.getOutputStream())
            );
        }


        /**
         * Disconnect the client from the server.
         */
        public void disconnect() {
            is_connected = false;
        }


        @Override
        public void run() {
            is_connected = true;
            try {
                LogHelper.debugLog("Just connected to client " + client_socket_address);
                app.sendToConsole(LogHelper.log(
                        "Just connected to client " + client_socket_address,
                        LogTypes.SERVER
                        ));
                // connect input and output streams for communication and send feedback to the client
                setIO();

                // The client mainloop.
                String message;
                while (is_connected) {

                    // detect if the input buffer is not empty.
                    if (input.ready()) {
                        message = input.readLine();
                        app.sendToConsole(LogHelper.log(message, LogTypes.CLIENT));

                        // detect if a client disconnects.
                        if (message.equals("disconnect")) {
                            LogHelper.debugLog("Closing connection for " + client_socket_address);
                            app.sendToConsole(LogHelper.log(
                                    "Closing connection for " + client_socket_address,
                                    LogTypes.SERVER
                            ));
                            disconnect();
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                closeAll();
                removeClient(this);
                sendClientListUpdate();
            }
        }


        /**
         * Returns the address of the client socket.
         * @return The remote address of the client socket in String
         */
        public String getClientSocketAddress() {
            return client_socket_address;
        }


        /**
         * Properly close the client. Checking if each client is null before closing.
         */
        private void closeAll() {
            try {
                if (input != null) {
                    app.sendToConsole(LogHelper.log(
                            "Closing input for " + client_socket_address,
                            LogTypes.SERVER
                    ));
                    input.close();
                }
                if (output != null) {
                    app.sendToConsole(LogHelper.log(
                            "Closing output for " + client_socket_address,
                            LogTypes.SERVER
                    ));
                    output.close();
                }
                if (client_socket != null) {
                    app.sendToConsole(LogHelper.log(
                            "Closing socket for" + client_socket_address,
                            LogTypes.SERVER
                    ));
                    client_socket.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                app.sendToConsole(LogHelper.log(
                        "Error closing connection from client.",
                        LogTypes.ERROR
                        ));
            }
            app.sendToConsole(LogHelper.log(
                    "Successfully closed connection for " + client_socket_address,
                    LogTypes.SERVER));
        }
    }
}