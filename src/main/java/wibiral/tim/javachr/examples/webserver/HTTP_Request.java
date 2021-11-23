package wibiral.tim.javachr.examples.webserver;

import java.net.Socket;

public class HTTP_Request {
    private String request;
    private Socket sender;

    public HTTP_Request(String request, Socket sender){
        this.request = request;
        this.sender = sender;
    }

    public String getRequest(){
        return request;
    }

    public Socket getSender(){
        return sender;
    }

    public boolean isValid() {
        return false;
    }

    public HTTP_Response getResponse() {
        return null;
    }
}
