package server;

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
    private final ArrayList<FSClient> clients_list = new ArrayList<>();
    private final MainWindow app;
    private boolean is_running;


    /**
     * @param hostname the fixed host name.
     * @param port the fixed port number.
     * @exception IOException error when opening the socket.
     */
    public ServerManager(MainWindow mw, String hostname, int port) throws IOException {
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
                clients_list.add(client);
                new Thread(client).start();
            }
            catch (IOException e) {
                try {
                    stopServer();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                LogHelper.debugLog("Server stopped.");
            }
        }
    }


    /**
     * Close the server.
     */
    public void stopServer() throws IOException {
        app.sendToConsole(LogHelper.log("Server closed.", LogTypes.INFO));
        is_running = false;
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
        return clients_list;
    }


    /**
     * Remove the client from the clients list upon disconnection.
     * @param client a client socket.
     */
    public void removeClient(FSClient client) {
        clients_list.remove(client);
    }


    /**
     * The FSClient represents a Fingerprint Scanner Client. Every client object
     * runs in a new thread created by the server.
     */
    private class FSClient extends Thread {
        private final Socket client_socket;
        private BufferedReader input;
        private BufferedWriter output;
        private boolean is_connected;


        /**
         * @param socket the client socket that connected to the server.
         */
        public FSClient(Socket socket) {
            client_socket = socket;
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
        private void disconnect() {
            is_connected = false;
        }


        @Override
        public void run() {
            is_connected = true;
            try {
                LogHelper.debugLog("Just connected to client " + client_socket.getRemoteSocketAddress());
                app.sendToConsole(LogHelper.log(
                        "Just connected to client " + client_socket.getRemoteSocketAddress(),
                        LogTypes.SERVER
                        ));
                // connect input and output streams for communication and send feedback to the client
                setIO();
                // TODO: remove next three output lines.
                output.write("[server] You are connected to " + client_socket.getLocalSocketAddress());
                output.newLine();
                output.flush();

                /* TODO: create a main loop. the main loop will listen for data from the client.*/
                String message;
                while (is_connected) {
                    message = input.readLine();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                LogHelper.debugLog("Closing connection for " + client_socket.getRemoteSocketAddress());
                app.sendToConsole(LogHelper.log(
                        "Closing connection for " + client_socket.getRemoteSocketAddress(),
                        LogTypes.SERVER
                        ));
                closeAll(client_socket, input, output);
                removeClient(this);
            }
        }


        /**
         * Properly close the client. Checking if each client is null before closing.
         *
         * @param socket the client socket.
         * @param input the input stream of the client.
         * @param output the output stream of the client.
         */
        private void closeAll(Socket socket, BufferedReader input, BufferedWriter output) {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
                if (socket != null) {
                    socket.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
                app.sendToConsole(LogHelper.log(
                        "Error closing connection from client.",
                        LogTypes.ERROR
                        ));
            }
            app.sendToConsole(LogHelper.log("Connection has been closed.", LogTypes.SERVER));
        }
    }
}