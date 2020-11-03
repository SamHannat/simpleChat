import client.ChatClient;
import common.ChatIF;

import java.io.IOException;
import java.util.Scanner;

public class ServerConsole implements ChatIF {

    public void display(String message)
    {
        System.out.println("SERVER MSG> " + message);
    }
    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;
    /**
     * The server instance that is running.
     */
    EchoServer server;

    /**
     * Scanner that listens to server user input.
     */
    Scanner fromConsole;

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the server's message handler.
     */
    public void accept()
    {
        try
        {

            String message;

            while (true)
            {
                message = fromConsole.nextLine();
                if(message.strip().charAt(0) == '#') {
                    message = message.strip();

                    switch (message) {
                        case "#quit":
                            server.close();
                            System.exit(0);
                            break;
                        case "#stop":
                            server.stopListening();
                            break;
                        case "#close":
                            server.close();
                            break;
                        case "#getPort":
                            this.display(Integer.toString(server.getPort()));
                            break;
                        case "#start":
                            if(server.isListening()) {
                                this.display("The server is already listening for clients.");
                            } else {
                                server.listen();
                                this.display("Started listening for clients.");
                            }
                            break;
                        default:
                            if(message.startsWith("#setport")) {
                                if(server.isListening()) {
                                    this.display("The server is currently listening for clients. Please stop it before changing the port.");
                                } else {
                                    try {
                                        int port = Integer.parseInt(message.split(" ")[1]);
                                        server.setPort(port);
                                        this.display("Port now set to " + port);
                                    } catch (Exception e) {
                                        this.display("Invalid port.");
                                    }
                                }

                            } else {
                                this.display("Unknown command");
                            }

                    }


                } else {
                    server.sendToAllClients("SERVER MSG>" + message);
                    this.display(message);
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }


    /**
     * Constructs an instance of the ServerConsole.
     *
     * @param port The port to connect on.
     */
    public ServerConsole(int port)
    {

        server = new EchoServer(port);
        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
        try
        {
            server.listen(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    /**
     * This method is responsible for the creation of the Server Console.
     *
     * @param args[0] The default port
     */
    public static void main(String[] args)
    {
        int port = 0; //Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ServerConsole sv = new ServerConsole(port);
        sv.accept();
        try
        {
            sv.server.listen(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }

    }
}
