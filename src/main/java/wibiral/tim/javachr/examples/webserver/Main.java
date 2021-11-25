package wibiral.tim.javachr.examples.webserver;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;
import wibiral.tim.javachr.rules.head.Head;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Accept connection (ONLY ACCEPTS ONE!)
        Rule acceptConnection = new Simplification("Accept Connection", Head.OF_TYPE(ServerSocket.class))
                .body((head, newConstraints) -> {   // Add connection constraint if connection incoming.
                    try {
                        Socket conn = ((ServerSocket) head[0].value()).accept();
                        if (conn != null)
                            newConstraints.add(new Constraint<>(new Connection(conn)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        // Receive some bytes
        Rule readFromConnection = new Propagation("Read from connection", Head.OF_TYPE(Connection.class))
                .guard(head -> ((Connection) head[0].value()).hasReceivedBytes())
                .body(((head, newConstraints) -> ((Connection) head[0].value()).readAll()));

        // Parse request, kick it if invalid, otherwise create HTTP Request object
        Rule parseRequest = new Simplification("Parse request", Head.OF_TYPE(Connection.class))
                .guard(head -> ((Connection) head[0].value()).isMessageComplete())
                .body((head, newConstraints) -> {
                    Connection connection = (Connection) head[0].value();
                    if (connection.isValidRequest()) {
                        newConstraints.add(new Constraint<>(connection.getRequest()));
                    } else {
                        System.err.println("Invalid request. Some error occurred!");
                    }
                    connection.close(); // Close after the request was received
                });

        // Create response object
        Rule createResponse = new Simplification("Create response", Head.OF_TYPE(HTTP_Request.class))
                .body((head, newConstraints) -> {
                    HTTP_Request request = (HTTP_Request) head[0].value();
                    newConstraints.add(new Constraint<>(request.getResponse()));
                });

        // Send response
        Rule sendResponse = new Simplification("Send response", Head.OF_TYPE(HTTP_Response.class))
                .body((head, newConstraints) -> {
                    HTTP_Response response = (HTTP_Response) head[0].value();
                    try {
                        OutputStreamWriter out = new OutputStreamWriter(response.getSender().getOutputStream());
                        out.write(response.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(10);  // makes accept() block only 10ms

            ConstraintSolver solver = new SimpleConstraintSolver(acceptConnection,
                                                                 readFromConnection,
                                                                 parseRequest,
                                                                 createResponse,
                                                                 sendResponse);
            solver.solve(serverSocket);
        }
    }
}
