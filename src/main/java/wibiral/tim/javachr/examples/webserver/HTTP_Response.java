package wibiral.tim.javachr.examples.webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class HTTP_Response {
    private static final String HEADER_OK = "HTTP/1.1 200 OK\r\n" +
                                            "Server: JavaCHR WebServer\r\n";

    private static final String HEADER_404 = "HTTP/1.1 404 Not Found\r\n" +
                                            "Server: JavaCHR WebServer\r\n" +
                                            "\r\n" +
                                            "<html>\n" +
                                            "<head><title>404 Not Found</title></head>\n" +
                                            "<body bgcolor=\"white\">\n" +
                                            "<center><h1>404 Not Found</h1></center>\n" +
                                            "<hr><center>JavaCHR WebServer</center>\n" +
                                            "</body>\n" +
                                            "</html>";

    private final Socket sender;
    private final char[] response;

    HTTP_Response(Socket sender, String method, String resource) {
        this.sender = sender;

        if (method.toLowerCase(Locale.ROOT).equals("head")) {
            this.response = buildHeadResponse(resource);

        } else {    // get-response is default for unknown requests
            this.response = buildGetResponse(resource);
        }
    }

    public Socket getSender() {
        return sender;
    }

    public char[] getResponse(){
        return response;
    }

    @Override
    public String toString() {
        return new String(response);
    }

    private char[] buildHeadResponse(String resource) {
        File f = new File(resource);
        if(!f.exists() || f.isDirectory())
            return HEADER_404.toCharArray();

        return createHeader(f.length(), f.getName()).toCharArray();
    }

    private char[] buildGetResponse(String resource) {
        File f = new File(resource);
        if(!f.exists() || f.isDirectory())
            return HEADER_404.toCharArray();

        long contentLength = f.length();
        String header = createHeader(contentLength, f.getName());

        int responseLength = (int) (header.length() + contentLength);
        char[] responseArray = new char[responseLength];

        for (int i = 0; i < header.length(); i++) {
            responseArray[i] = header.charAt(i);
        }

        try(FileReader in = new FileReader(f)){
            in.read(responseArray, header.length(), responseLength);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseArray;
    }

    private String createHeader(long contentLength, String fileName){
        StringBuilder responseBuilder = new StringBuilder(HEADER_OK);
        responseBuilder.append("Content-length: ").append(contentLength);

        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            responseBuilder.append("Content-type: image/jpg");
        else
            responseBuilder.append("Content-type: text/html");

        responseBuilder.append("\r\n"); // End header

        return responseBuilder.toString();
    }
}
