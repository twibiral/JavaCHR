package wibiral.tim.javachr.examples.webserver;

import wibiral.tim.javachr.ConstraintSolver;
import wibiral.tim.javachr.SimpleConstraintSolver;
import wibiral.tim.javachr.constraints.Constraint;
import wibiral.tim.javachr.rules.Propagation;
import wibiral.tim.javachr.rules.Rule;
import wibiral.tim.javachr.rules.Simplification;
import wibiral.tim.javachr.rules.head.Head;
import wibiral.tim.javachr.tracing.CommandLineTracer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Main {
    public static final int PORT = 8080;
    public static final Logger LOG = Logger.getLogger("ServerLog");

    public static void main(String[] args) throws IOException {
        // Accept connection (ONLY ACCEPTS ONE!)
        Rule acceptConnection = new Propagation("Accept Connection", Head.OF_TYPE(ServerSocket.class))
                .body((head, newConstraints) -> {   // Add connection constraint if connection incoming.
                    try {
                        Socket conn = ((ServerSocket) head[0].value()).accept();
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
        Rule readFromConnection = new Propagation("Read from connection", Head.OF_TYPE(Connection.class))
                .guard(head -> ((Connection) head[0].value()).hasReceivedBytes())
                .body(((head, newConstraints) -> {
                    LOG.info("Read from connection");
                    ((Connection) head[0].value()).readAll();
                    LOG.info("Read complete");
                }));

        // Kick incomplete corrupt messages
        Rule kickIncomplete = new Simplification("Kick incomplete", Head.OF_TYPE(Connection.class))
                .guard(head -> !((Connection) head[0].value()).isMessageComplete());

        // Parse request, kick it if invalid, otherwise create HTTP Request object
        Rule parseRequest = new Simplification("Parse request", Head.OF_TYPE(Connection.class))
                .guard(head -> ((Connection) head[0].value()).isMessageComplete())
                .body((head, newConstraints) -> {
                    Connection connection = (Connection) head[0].value();
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
        Rule createResponse = new Simplification("Create response", Head.OF_TYPE(HTTP_Request.class))
                .body((head, newConstraints) -> {
                    HTTP_Request request = (HTTP_Request) head[0].value();
                    HTTP_Response response = request.getResponse();
                    newConstraints.add(new Constraint<>(response));

                    LOG.info("Created response: " + response.toString().split("\r\n")[0]);
                });

        // Send response
        Rule sendResponse = new Simplification("Send response", Head.OF_TYPE(HTTP_Response.class))
                .body((head, newConstraints) -> {
                    HTTP_Response response = (HTTP_Response) head[0].value();
                    assert !response.getSender().isClosed();
                    try (Socket client = response.getSender();
                            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {

                        System.out.println(response.getResponse());
                        out.write(response.getResponse());
                        out.flush();

                        LOG.info("Response sent successfully!");

                    } catch (IOException e) {
                        LOG.warning("Sending response failed: " + e);
                        e.printStackTrace();
                    }
                });

        LOG.info("Start server on port " + PORT + " ...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//            serverSocket.setSoTimeout(10);  // makes accept() block only 10ms

            ConstraintSolver solver = new SimpleConstraintSolver(acceptConnection,
                                                                 readFromConnection,
                                                                 parseRequest,
                                                                 createResponse,
                                                                 sendResponse);
            solver.setTracer(new CommandLineTracer());
            solver.solve(serverSocket);

            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
