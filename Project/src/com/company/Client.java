package com.company;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.Base64;

public class Client {
    private Socket sock;

    public Client() {
        sock = new Socket();
    }

    public void connect(String ip, int port, boolean lb) {
        try {
            sock = new Socket(ip, port);
            sock.setSoTimeout(5000);

            if (!lb) {
                while (true) {
                    try {
                        int choice = getRequestInput();

                        switch (choice) {
                            case 1:
                                handleDirectoryRequest();
                                break;
                            case 2:
                                handleFileTransferRequest();
                                break;
                            case 3:
                                handleComputationRequest();
                                break;
                            case 4:
                                handleVideoStreamingRequest();
                                break;
                            default:
                                System.out.println("Invalid request type.");
                        }

                        System.out.print("\nDo you want to make another request? (Y/N): ");
                        String continueInput = new BufferedReader(new InputStreamReader(System.in)).readLine().toLowerCase();
                        if (continueInput.equals("n")) {
                            sock.close();
                            break;
                        }
                    } catch (SocketTimeoutException | InterruptedException e) {
                        System.out.println("Server timeout. Returning to the load balancer...");
                        sock.close();
                        connect(ip, port, true);
                    }
                }
            } else {
                requestServerLocation(ip, port);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestServerLocation(String ip, int port) throws IOException {
        System.out.println("Asking the load balancer for the server IP...");
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

        out.println("req_sv_loc");
        String response = in.readLine();

        if (response != null) {
            String[] newLoc = response.split(":");
            if (newLoc.length == 2) {
                sock.close();
                System.out.println("Connecting to the server with address: " + newLoc[0] + ":" + newLoc[1]);
                connect(newLoc[0], Integer.parseInt(newLoc[1]), false);
            }
        } else {
            System.out.println("No server location was returned, none accessible.");
            sock.shutdownOutput();
            sock.close();
        }
    }

    public void handleDirectoryRequest() throws IOException {
        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

        out.println("req_dir");
        String response = in.readLine();

        if (response != null) {
            System.out.println("Server's directory: " + response);
        } else {
            System.out.println("No directory was returned or directory was empty.");
        }
    }

    public void handleFileTransferRequest() throws IOException, InterruptedException {
        System.out.print("\nEnter name of the file you want from the server with its extension (e.g., 'file.txt'): ");
        String fileName = new BufferedReader(new InputStreamReader(System.in)).readLine();

        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(fileName.split("\\.")[0] + "_new." + fileName.split("\\.")[1]));
        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

        out.println("req_file%" + fileName);

        while (true) {
            System.out.println("Receiving the file...");
            String data = in.readLine();
            if (data.equals("DONE")) {
                sock.shutdownOutput();
                sock.close();
                fileOut.close();
                System.out.println("File was successfully downloaded from the server.");
                break;

            } else if (data.equals("NONE")) {
                sock.shutdownOutput();
                sock.close();
                fileOut.close();
                System.out.println("No file was returned from the server or it does not exist.");
                break;
            } else {
                fileOut.write(data.getBytes());
                fileOut.flush();
                Thread.sleep(500);
            }
        }
    }

    public void handleComputationRequest() throws IOException {
        int secs;
        while (true) {
            System.out.print("\nHow many seconds do you want the computation to take: ");
            try {
                secs = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Enter a numeric value...");
            }
        }

        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
        out.println("req_comp%" + secs);
        System.out.println("Computation request is sent.");
    }

    public void handleVideoStreamingRequest() {
        try {
            System.out.print("Enter the video file name: ");
            String fileName = new BufferedReader(new InputStreamReader(System.in)).readLine();

            File file = new File("D:/TestDirectory/" + fileName);
            if (file.exists() && file.canRead()) {
                byte[] videoContent = Files.readAllBytes(file.toPath());
                String encodedVideo = Base64.getEncoder().encodeToString(videoContent);

                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println(encodedVideo);
                System.out.println("Video streaming request sent.");

                out.close();
            } else {
                System.out.println("The specified video file does not exist or cannot be read.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private int getRequestInput() {
        int choice;
        while (true) {
            System.out.print("Which request do you want to make?\n" +
                    "1) Request server's directory listing\n" +
                    "2) Request file\n" +
                    "3) Request a computation\n" +
                    "4) Video streaming\n" +
                    "Enter your choice: ");
            try {
                choice = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
                break;
            } catch (NumberFormatException | IOException e) {
                System.out.println("Select a valid option.");
            }
        }
        return choice;
    }
}
