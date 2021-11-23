package wibiral.tim.javachr.examples.webserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Connection {
    private Socket connectionSock;

    public Connection(Socket connectionSock) {
        this.connectionSock = connectionSock;
    }

    public boolean receivedBytes(){
        try {
            return connectionSock.getInputStream().available() > 0;

        } catch (IOException e) {
            return false;
        }
    }

    public void readAll(){

    }

    public boolean messageComplete(){
        return false;
    }

    public HTTP_Request getRequest(){
        return null;
    }

    public void close(){

    }

    public boolean isValidRequest() {
        return false;
    }
}
