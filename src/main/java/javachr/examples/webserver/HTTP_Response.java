package javachr.examples.webserver;

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
    private final byte[] response;

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

    public byte[] getResponse(){
        return response;
    }

    @Override
    public String toString() {
        return new String(response).split("\n")[0];
    }

    private byte[] buildHeadResponse(String resource) {
        File f = new File(resource);
        if(!f.exists() || f.isDirectory())
            return HEADER_404.getBytes(StandardCharsets.UTF_8);

        return createHeader(f.length(), f.getName()).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] buildGetResponse(String resource) {
        File f = new File(resource);
        if(!f.exists() || f.isDirectory())
            return HEADER_404.getBytes(StandardCharsets.UTF_8);

        int contentLength = (int) f.length();
        byte[] header = createHeader(contentLength, f.getName()).getBytes(StandardCharsets.UTF_8);

        int responseLength = header.length + contentLength;
        byte[] responseArray = new byte[responseLength];

        // copy header into the array
        System.arraycopy(header, 0, responseArray, 0, header.length);

        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))){
            in.read(responseArray, header.length, contentLength);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseArray;
    }

    private String createHeader(long contentLength, String fileName){
        StringBuilder responseBuilder = new StringBuilder(HEADER_OK);
        responseBuilder.append("Content-length: ").append(contentLength).append("\r\n");

        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            responseBuilder.append("Content-type: image/jpg\r\n");
        else
            responseBuilder.append("Content-type: text/html\r\n");

        responseBuilder.append("\r\n"); // End header
        return responseBuilder.toString();
    }
}
