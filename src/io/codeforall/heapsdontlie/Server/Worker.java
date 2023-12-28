package io.codeforall.heapsdontlie.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Worker implements Runnable {
    private String name;
    private Socket socket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;

    public Worker(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;

    }

    @Override
    public void run() {

        askForName();

        server.echo("*** " + name + " joined the chat ***");

        sendMessage();

    }

    private void askForName() {
        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write("What's your name?\n");
            out.flush();

            name = in.readLine();

            if (name == null) {
                System.out.println("Connection lost...");
                server.remove(this);
                Thread.currentThread().stop();
            }
/*
        } catch (SocketException e) {

            System.out.println("Connection lost...");

            Thread.currentThread().stop();


 */
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void sendMessage() {

        try {

            String message;

            while ((message = in.readLine()) != null && !message.equals("/quit")) {

                server.echo(name + ": " + message);

            }

            server.echo("*** " + name + " left the chat ***");
            server.remove(this);
            socket.close();

        } catch (SocketException e) {

            server.echo("*** " + name + " left the chat ***");
            server.remove(this);

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("here");
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }
}
