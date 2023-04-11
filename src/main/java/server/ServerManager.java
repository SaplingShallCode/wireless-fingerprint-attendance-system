package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;


/**
 * The ServerManager class will handle communication with all clients.
 * These clients must be fingerprint scanners. The server will serve
 * clients data from the database and also update the database data.
 * The server should handle multiple clients therefore it must
 * implement the Runnable interface.
 */
public class ServerManager implements Runnable{
    private ServerSocket serversocket;

    /**@param hostname the fixed host name.
     * @param port the fixed port number.
     * @exception IOException error when opening the socket.
     */
    public ServerManager(String hostname, int port) throws IOException{
        serversocket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(hostname, port);
        serversocket.bind(address);
    }


    /**
     * */
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("[info] Waiting for a connection on port " + serversocket.getLocalPort());
                // next line blocks current thread while waiting for a client to connect.
                Socket server = serversocket.accept();
                System.out.println("[info] just connected to client: " + server.getRemoteSocketAddress());
                /* TODO: create a table in the database for fingerprint scanner clients. will be used for
                *   client verification. */

                // connect input and output streams for communication and send feedback to the client
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(server.getInputStream())
                );
                BufferedWriter output = new BufferedWriter(
                        new OutputStreamWriter(server.getOutputStream())
                );
                output.write("[server] You are connected to " + server.getLocalSocketAddress());
                output.newLine();
                output.flush();

                /* TODO: create a main loop. the main loop will listen for data from the client.*/
                while (true) {
                    break;
                }

                System.out.println("[info] closing server...");
                input.close();
                output.close();
                server.close();

            }
            catch (IOException e) {
                // will occur when waiting for connection from serversocket.accept()
                e.printStackTrace();
                break;
            }
        }
    }
}
