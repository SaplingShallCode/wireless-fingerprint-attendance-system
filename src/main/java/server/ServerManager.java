package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

/**
 * The ServerManager class will listen for clients trying to connect.
 * These clients are be fingerprint scanners. The server is able to
 * handle multiple clients. The server will serve clients data from
 * the database and also update the database data.
 */
public class ServerManager{
    private ServerSocket serversocket;

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

    /**
     * Run the server.
     */
    public void runServer() {
        while (true) {
            try {
                System.out.println("[info] Waiting for a connection on port " + serversocket.getLocalPort());
                // blocks current thread while waiting for a client to connect.
                Socket client = serversocket.accept();

                /* TODO: create a table in the database for fingerprint scanner clients. will be used for
                 *   client verification. */

                // create a thread for the connected client and run the thread.
                ClientHandler client_handler = new ClientHandler(client);
                new Thread(client_handler).start();
            }
            catch (IOException e) {
                // will occur when waiting for connection from serversocket.accept()
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Close the server.
     */
    public void stopServer() {
        try {
            serversocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
