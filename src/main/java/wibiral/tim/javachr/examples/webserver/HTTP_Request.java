package wibiral.tim.javachr.examples.webserver;

import java.io.File;
import java.net.Socket;
import java.util.Locale;

public class HTTP_Request {
    private static final String RESOURCE_PREFIX = "./html/";

    private final Socket sender;
    private final String method;
    private String resource;


    public HTTP_Request(Socket sender, String method, String resource, String protocol){
        this.sender = sender;
        this.method = method.trim().toLowerCase(Locale.ROOT);

        this.resource = resource.trim().equals("/") ? "index.html" : resource.trim().toLowerCase(Locale.ROOT); // map "/" to "index.html"
        if(this.resource.startsWith("/"))
            this.resource = resource.substring(1, this.resource.length()); // Remove first "/"
        this.resource = RESOURCE_PREFIX + this.resource;
    }

    public Socket getSender(){
        return sender;
    }

    public boolean isValid() {
        boolean methodAccepted = method.equals("get") || method.equals("head");
        File f = new File(resource);
        boolean fileExists = f.exists() && !f.isDirectory();

        return methodAccepted && fileExists;
    }

    public HTTP_Response getResponse() {
        return new HTTP_Response(sender, method, resource);
    }

    @Override
    public String toString() {
        return "HTTP_Request{" +
                "sender=" + sender +
                ", method='" + method + '\'' +
                ", resource='" + resource + '\'' +
                '}';
    }
}
