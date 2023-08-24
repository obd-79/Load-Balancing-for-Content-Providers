package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Server implements Runnable {
    private int id;
    private boolean isBusy;
    private String loadBalancingMethod;
    private Socket clientSocket;

    public Server(int id, String loadBalancingMethod, Socket clientSocket) {
        this.id = id;
        this.isBusy = false;
        this.loadBalancingMethod = loadBalancingMethod;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = in.readLine();
            String[] requestParts = request.split("%");

            String requestType = requestParts[0];
            String fileName = (requestParts.length > 1) ? requestParts[1] : null;

            handleRequest(requestType, fileName, out);

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(String requestType, String fileName, PrintWriter out) {
        switch (requestType) {
            case "req_dir":
                handleDirectoryListingRequest(out);
                break;
            case "req_file":
                handleFileTransferRequest(fileName, out);
                break;
            case "req_comp":
                handleComputationRequest(Integer.parseInt(fileName));
                break;
            case "req_video":
                handleVideoStreamingRequest(fileName, out);
                break;
            default:
                System.out.println("Invalid request type.");
        }
    }

    private void handleDirectoryListingRequest(PrintWriter out) {
        File directory = new File("D:/TestDirectory/");
        String[] files = directory.list();
        if (files != null) {
            out.println(String.join(",", files));
        } else {
            out.println("NONE");
        }
    }

    private void handleFileTransferRequest(String fileName, PrintWriter out) {
        File file = new File("D:/TestDirectory/" + fileName);
        if (file.exists() && file.canRead()) {
            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                out.println(new String(fileContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            out.println("NONE");
        }
    }

    private void handleComputationRequest(int duration) {
        this.isBusy = true;
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.isBusy = false;
    }

    private void handleVideoStreamingRequest(String fileName, PrintWriter out) {
        File file = new File("D:/TestDirectory/" + fileName);
        if (file.exists() && file.canRead()) {
            try {
                byte[] videoContent = Files.readAllBytes(file.toPath());
                out.println(new String(videoContent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            out.println("NONE");
        }
    }

    public String registerWithLoadBalancer() {
        return "Server " + id + " has joined with " + loadBalancingMethod + " method.";
    }

    public String terminate() {
        this.isBusy = true;
        return "Server " + id + " has terminated.";
    }

    public int getId() {
        return id;
    }

    public boolean isBusy() {
        return isBusy;
    }
}