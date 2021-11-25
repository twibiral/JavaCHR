package wibiral.tim.javachr.examples.webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connection {
    private final Socket connectionSock;
    private final StringBuilder message = new StringBuilder();
    private boolean messageComplete = false;

    // Information collected from the data read:
    private final List<String> headerLines = new ArrayList<>();
    private final StringBuilder body = new StringBuilder();
    private long bodyCounter = 0;

    // True if the header was already parsed
    private boolean headerRead = false;

    // Extracted information
    private long contentLength = 0;
    private String method = null;
    private String resource = null;
    private String protocol = null;
    private final Map<String, String> headers = new HashMap<>();

    public Connection(Socket connectionSock) {
        this.connectionSock = connectionSock;
    }

    public boolean hasReceivedBytes() {
//        try {
//            return connectionSock.getInputStream().available() > 0;
//
//        } catch (IOException e) {
//            return false;
//        }
        return true;
    }

    public void readAll(){
        // Header complete; read into body
        if(headerRead) {
//            try (BufferedInputStream in = new BufferedInputStream(connectionSock.getInputStream())){
//                if (in.available() > 0){
//                    byte[] received = new byte[in.available()];
//                    in.read(received);
//                    body.append(received);
//                }
//
//                messageComplete = body.length() >= contentLength;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return;
        }

        // read header
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
            String line = in.readLine();
            message.append(line);
            messageComplete = appendLine(line);

            while (line != null && !messageComplete){
                line = in.readLine();
                message.append(line);
                messageComplete = appendLine(line) || line.equals("");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMessageComplete(){
        return messageComplete;
    }

    public HTTP_Request getRequest(){
        return new HTTP_Request(connectionSock, method, resource, protocol);
    }

    public void close(){
        try {
            connectionSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean appendLine(String line) {
        if(headerRead)
            return true;

        headerLines.add(line);
        if(headerLines.size() == 1) {
            // first line
            String[] split = line.split("\\s");
            if(split.length < 3){   // Incorrect header
                return false;
            }

            method = split[0].trim();
            resource = split[1].trim();
            protocol = split[2].trim();

        } else if(line.equals("\r\n") && contentLength == 0){ // && headerLines.size() > 1
            // End of header, request has no body
            headerRead = true;
            return true;

        } else if(line.equals("\r\n") && contentLength > 0) {
            // End of header, request has body
            headerRead = true;
            return true;

        } else if(line.contains(":")){
            // Attribute-Value-Pair
            String[] split = line.split(":");

            if(split[0].toLowerCase().trim().startsWith("content-length")){
                contentLength = Long.parseLong(split[1].trim());
            }

            headers.put(split[0].trim().toLowerCase(), split[1].trim());

        }

        return false;
    }
}
