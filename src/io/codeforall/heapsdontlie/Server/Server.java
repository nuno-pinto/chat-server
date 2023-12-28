package io.codeforall.heapsdontlie.Server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private List<Worker> workerList = Collections.synchronizedList(new LinkedList<>());

    public static void main(String[] args) {

        int portNumber = 8000;
        //int portNumber = args[0];

        Server server = new Server();

        server.listen(portNumber);

    }

    private void listen(int portNumber) {
        try {

            ServerSocket serverSocket = new ServerSocket(portNumber);

            ExecutorService clientPool = Executors.newCachedThreadPool();

            Socket workerSocket;

            Worker worker;

            System.out.println("Server is up, awaiting connections...");

            while (true) {

                workerSocket = serverSocket.accept();

                System.out.println("New connection established");

                worker = new Worker(workerSocket, this);

                workerList.add(worker);

                clientPool.submit(worker);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void echo(String message) {

        System.out.println(message);

        PrintWriter out;

        for (Worker worker : workerList) {
            try {
                if (message.split(":")[0].equals(worker.getName())) {
                    continue;
                }

                out = new PrintWriter(worker.getSocket().getOutputStream());

                out.write(message + "\n");
                out.flush();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void remove(Worker worker) {
        workerList.remove(worker);
    }
}
