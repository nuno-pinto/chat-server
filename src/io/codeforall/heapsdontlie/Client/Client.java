package io.codeforall.heapsdontlie.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {


    public static void main(String[] args) {

        String hostName = "127.0.0.1";
        //String hostName = args[0];

        int portNumber = 8000;
        //int portNumber = args[1];

        Client client = new Client();

        client.init(hostName, portNumber);

    }


    private void init(String hostName, int portNumber) {

        System.out.println("Attempting to connect to server...");

        try {

            Socket clientSocket = new Socket(hostName, portNumber);

            System.out.println("Connection established with server\n");

            ExecutorService fixedPool = Executors.newFixedThreadPool(2);

            fixedPool.submit(new ReceiveMessage(clientSocket));

            fixedPool.submit(new SendMessage(clientSocket));

        } catch (ConnectException e) {

            System.out.println("Unable to establish connection with server");

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private class ReceiveMessage implements Runnable {

        private Socket clientSocket;

        public ReceiveMessage(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;

                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }

                in.close();

                System.exit(0);

            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    private class SendMessage implements Runnable {

        private Socket clientSocket;
        public SendMessage(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

                String message;

                while ((message = in.readLine()) != null) {
                    out.println(message);
                    out.flush();
                }

                in.close();
                out.close();


            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
