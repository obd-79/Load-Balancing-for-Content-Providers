package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LoadBalancer {

    private static final class LoadBalancerThread implements Runnable {

        private final Socket clientSocket;
        private final List<Socket> serverSockets;
        private final int[] loads;

        private LoadBalancerThread(Socket clientSocket, List<Socket> serverSockets, int[] loads) {
            this.clientSocket = clientSocket;
            this.serverSockets = serverSockets;
            this.loads = loads;
        }

        @Override
        public void run() {
            try {
                Socket serverSocket = getNextServerSocket();
                forwardRequest(clientSocket, serverSocket);
                updateServerLoad(serverSocket);
                System.out.println(Arrays.toString(loads));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Socket getNextServerSocket() {
            int minLoad = Integer.MAX_VALUE;
            Socket selectedServer = null;
            for (Socket serverSocket : serverSockets) {
                int load = loads[serverSockets.indexOf(serverSocket)];
                if (load < minLoad) {
                    minLoad = load;
                    selectedServer = serverSocket;
                }
            }
            return selectedServer;
        }

        private void forwardRequest(Socket clientSocket, Socket serverSocket) throws IOException {
            Scanner clientReader = new Scanner(clientSocket.getInputStream());
            PrintWriter serverWriter = new PrintWriter(serverSocket.getOutputStream(), true);

            String request = clientReader.nextLine();
            serverWriter.println(request);

            clientReader.close();
            serverWriter.close();
        }

        private void updateServerLoad(Socket serverSocket) {
            int serverIndex = serverSockets.indexOf(serverSocket);
            synchronized (this) {
                loads[serverIndex]++;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final int[] serverPorts = {8000, 8001, 8002};
        final int[] loads = new int[serverPorts.length];
        final List<Socket> serverSockets = new ArrayList<>();

        for (int port : serverPorts) {
            serverSockets.add(new Socket("localhost", port));
        }

        final ServerSocket loadBalancerSocket = new ServerSocket(Integer.parseInt(args[0]));

        while (true) {
            Socket clientSocket = loadBalancerSocket.accept();
            new Thread(new LoadBalancerThread(clientSocket, serverSockets, loads)).start();
        }
    }
}
