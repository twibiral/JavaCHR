package wibiral.tim.javachr.examples.webserver;

import java.net.Socket;

public class HTTP_Response {
    private final Socket sender;
    private final String request;

    HTTP_Response(Socket sender, String request) {
        this.sender = sender;
        this.request = request;
    }

    public Socket getSender() {
        return null;
    }

    public String getResponse() {
        return "";
    }
}
