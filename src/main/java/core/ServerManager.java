package core;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import gui.MainWindow;
import utility.Const;
import utility.LogHelper;
import utility.LogTypes;


/**
 * The ServerManager class will listen for clients trying to connect.
 * These clients are be fingerprint scanners. The server is able to
 * handle multiple clients. The server will serve clients data from
 * the database and also update the database data.
 */
@SuppressWarnings({"unused", "noinspection", "LoopConditionNotUpdatedInsideLoop", "StatementWithEmptyBody"})
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
                app.sendToConsole(LogHelper.log("Disconnecting all clients from the server...", LogTypes.WARNING));
                for (FSClient client : fsclients) {
                    if (client != null) {
                        client.interrupt();
                    }
                }

                // wait for all clients to be removed before proceeding
                while (fsclients.size() != 0);
                app.sendToConsole(LogHelper.log("All clients have been disconnected.", LogTypes.INFO));
                app.sendToConsole(LogHelper.log("Server sucessfully closed.", LogTypes.INFO));
                LogHelper.debugLog("Server stopped.");
            }
            finally {
                sendClientListUpdate();
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
        app.sendToConsole(LogHelper.log("Closing server...", LogTypes.WARNING));
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
        private final String client_name;


        /**
         * @param socket the client socket that connected to the server.
         */
        public FSClient(Socket socket) {
            client_socket = socket;
            client_socket_address = client_socket.getRemoteSocketAddress().toString();
            client_name = generateClientName();
        }


        /**
         * Set the Input and Output streams of the client.
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
                app.sendToConsole(LogHelper.log(
                        "Just connected to client " + client_socket_address, LogTypes.SERVER
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

                        // Events
                        switch (message) {
                            case "disconnect" -> {
                                app.sendToConsole(LogHelper.log(
                                        "Closing connection for client " + client_name, LogTypes.SERVER
                                ));
                                disconnect();
                            }
                            case "enrollFinger" -> {
                                String first_name = input.readLine();
                                String middle_name = input.readLine();
                                String last_name =  input.readLine();
                                String age = input.readLine();
                                String gender = input.readLine();
                                String phone_number = input.readLine();
                                String address = input.readLine();
                                String finger_id_unparsed = input.readLine();

                                DatabaseManager databaseManager = new DatabaseManager();

                                try {
                                    Connection connection = databaseManager.openConnection();
                                    Statement statement = connection.createStatement();
                                    String enroll = "INSERT INTO user_info (user_id, age, gender, phone_number, address, " +
                                            "last_name, first_name, middle_name) VALUES (";
                                    String insert = enroll + finger_id_unparsed + ", "
                                            + age + ", "
                                            + gender + ", "
                                            + phone_number + ", "
                                            + address + ", "
                                            + last_name + ", "
                                            + first_name + ", "
                                            + middle_name + ")";
                                    statement.executeUpdate(insert);

                                    statement.close();
                                    connection.close();

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                // TODO: Enroll to database here.

                                app.sendToConsole(LogHelper.log(
                                        "Successfully enrolled: " +
                                                first_name + " " +
                                                middle_name + " " +
                                                last_name,
                                        LogTypes.CLIENT
                                ));
                                app.sendToConsole(LogHelper.log(
                                        "=======================     INFORMATION     =======================",
                                        LogTypes.CLIENT
                                ));
                                app.sendToConsole(LogHelper.log("First Name : " + first_name, LogTypes.CLIENT));
                                app.sendToConsole(LogHelper.log("Middle Name: " + middle_name, LogTypes.CLIENT));
                                app.sendToConsole(LogHelper.log("Last Name  : " + last_name, LogTypes.CLIENT));
                                app.sendToConsole(LogHelper.log("Age        : " + age, LogTypes.CLIENT));
                                app.sendToConsole(LogHelper.log("Gender     : " + gender, LogTypes.CLIENT));
                                app.sendToConsole(LogHelper.log("Phone No.  : " + phone_number, LogTypes.CLIENT));
                                app.sendToConsole(LogHelper.log("Address    : " + address, LogTypes.CLIENT));
                            }
                            case "scanFinger" -> {
                                String finger_id_unparsed = input.readLine();
                                app.sendToConsole(LogHelper.log(
                                        "Found match with finger ID: " + finger_id_unparsed,
                                        LogTypes.CLIENT
                                ));
                                // TODO: Check if the ID is stored in the database.







                            }
                        }
                    }
                    if (this.isInterrupted()) {
                        throw new InterruptedException();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException ie) {
                app.sendToConsole(LogHelper.log(
                        "Forced to close connection with client " + client_name,
                        LogTypes.WARNING
                ));
                sendCommand("disconnect");
                disconnect();
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


        public String getClientName() {
            return client_name;
        }


        /**
         * Properly close the client. Checking if each client is null before closing.
         */
        private void closeAll() {
            try {
                if (input != null) {
                    app.sendToConsole(LogHelper.log(
                            "Closing input for client " + client_name,
                            LogTypes.SERVER
                    ));
                    input.close();
                }
                if (output != null) {
                    app.sendToConsole(LogHelper.log(
                            "Closing output for client " + client_name,
                            LogTypes.SERVER
                    ));
                    output.close();
                }
                if (client_socket != null) {
                    app.sendToConsole(LogHelper.log(
                            "Closing socket for client " + client_name,
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
                    "Successfully closed connection for client " + client_name,
                    LogTypes.SERVER));
        }


        /**
         * Generate an 8-character client name.
         * @return the generated client name.
         * @implNote this method is only used in the constructor of the client.
         */
        private String generateClientName() {
            int random_int;
            char random_character;
            StringBuilder generated_client_name = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                random_int = ThreadLocalRandom.current().nextInt(0, Const.CHARSET.length());
                random_character = Const.CHARSET.charAt(random_int);
                generated_client_name.append(random_character);
            }

            return generated_client_name.toString();
        }


        /**
         * Send a command to the client.
         * This method is primarily used by the CommandExecutor class.
         * @param command the command to be sent.
         * @see CommandExecutor
         */
        public void sendCommand(String command) {
            try {
                output.write(command + "\n");
                output.flush();
            }
            catch (IOException ioe) {
                app.sendToConsole(LogHelper.log("Error sending command to " + client_name, LogTypes.ERROR));
            }
        }
    }
}