package com.lloseng.ocsf.simpleChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF{

    final public static int DEFAULT_PORT = 5555;

    EchoServer echoServer;

    // For starters, create a constructor that creates a dependency with EchoServer
    public ServerConsole(int port)
    {
        try{
            echoServer = new EchoServer(port, this);
            echoServer.listen();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Server failed to start");
            System.exit(1);
        }

    }
    //doesn't throw any exceptions so free to continue

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept()
    {
        try
        {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true)
            {
                message = fromConsole.readLine();
                echoServer.handleMessageFromConsole(message);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Unexpected error while reading from console!");
        }
    }

    public void display(String message) {
        System.out.println("Shumy@localhost> " + message);
    }

    /**
     * This method is responsible for the creation of
     * the server instance (there is no UI in this phase).
     *
     * @param args The port number to listen on.  Defaults to 5555
     *          if no argument is entered.
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

//        try
//        {
//
//        }
//        catch (Exception ex)
//        {
//            System.out.println("ERROR - Could not listen for clients!");
//        }
        ServerConsole console = new ServerConsole(port);
        System.out.println("Server online. Standing by");
        console.accept();
    }



}
