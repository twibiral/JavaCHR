package wibiral.tim.javachr.examples.webserver;

import wibiral.tim.javachr.RuleApplicator;
import wibiral.tim.javachr.SimpleRuleApplicator;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;
import wibiral.tim.javachr.rules.head.Head;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Main {
    public static final int PORT = 8080;
    public static final Logger LOG = Logger.getLogger("ServerLog");

    public static void main(String[] args) throws IOException {
        // Accept connection (ONLY ACCEPTS ONE!)
        Rule acceptConnection = new Simplification("Accept Connection", Head.ofType(ServerSocket.class))
                .body((head, newConstraints) -> {   // Add connection constraint if connection incoming.
                    try {
                        Socket conn = ((ServerSocket) head[0].get()).accept();
                        if (conn != null){
                            newConstraints.add(new Constraint<>(new Connection(conn)));
                            LOG.info("New connection opened");
                        }

                    } catch (IOException e) {
                        // Don't log; it just tells that accept timed out
                    }

                    newConstraints.add(head[0]);
                });

        // Receive some bytes
        Rule readFromConnection = new Propagation("Read from connection", Head.ofType(Connection.class))
                .guard(head -> ((Connection) head[0].get()).hasReceivedBytes())
                .body(((head, newConstraints) -> {
                    LOG.info("Read from connection");
                    ((Connection) head[0].get()).readAll();
                    LOG.info("Read complete");
                }));

        // Kick incomplete corrupt messages
        Rule kickIncomplete = new Simplification("Kick incomplete", Head.ofType(Connection.class))
                .guard(head -> !((Connection) head[0].get()).isMessageComplete());

        // Parse request, kick it if invalid, otherwise create HTTP Request object
        Rule parseRequest = new Simplification("Parse request", Head.ofType(Connection.class))
                .guard(head -> ((Connection) head[0].get()).isMessageComplete())
                .body((head, newConstraints) -> {
                    Connection connection = (Connection) head[0].get();
                    HTTP_Request request = connection.getRequest();

                    if (request.isValid()){
                        newConstraints.add(new Constraint<>(request));
                        LOG.info("Got request: \n" + connection.getRequest().toString());

                    } else {
                        LOG.warning("Got invalid request!");
                        connection.close();
                    }
                });

        // Create response object
        Rule createResponse = new Simplification("Create response", Head.ofType(HTTP_Request.class))
                .body((head, newConstraints) -> {
                    HTTP_Request request = (HTTP_Request) head[0].get();
                    HTTP_Response response = request.getResponse();
                    newConstraints.add(new Constraint<>(response));

                    LOG.info("Created response: " + response.toString().split("\r\n")[0]);
                });

        // Send response
        Rule sendResponse = new Simplification("Send response", Head.ofType(HTTP_Response.class))
                .body((head, newConstraints) -> {
                    HTTP_Response response = (HTTP_Response) head[0].get();
                    assert !response.getSender().isClosed();
                    try (Socket client = response.getSender();
                        BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream())) {

                        out.write(response.getResponse());
                        LOG.info("Response sent successfully!");

                    } catch (IOException e) {
                        LOG.warning("Sending response failed: " + e);
                        e.printStackTrace();
                    }
                });

        LOG.info("Start server on port " + PORT + " ...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(10);  // makes accept() block only 10ms

            RuleApplicator solver = new SimpleRuleApplicator(readFromConnection,
                                                                 parseRequest,
                                                                 kickIncomplete,
                                                                 createResponse,
                                                                 sendResponse,
                                                                 acceptConnection);
            solver.solve(serverSocket);
        }
    }
}
