package com.lloseng.ocsf.simpleChat;// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import com.lloseng.ocsf.server.*;

import java.io.*;
import java.net.ServerSocket;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    ServerConsole console;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ServerConsole console)
    {
        super(port);
        this.console = console;
    }


    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
    {
        if(msg.equals("heartbeat")){
            this.sendToAllClients("heartbeat");
        }
        else{
            System.out.println("Message received: " + msg + " from " + client);
            this.sendToAllClients(msg);
        }
    }

    /**
     * This method handles messages from the "terminal"
     *
     *
     */
    public void handleMessageFromConsole(String message)
    {
        try
        {
            if(message.startsWith("#")){
                message=message.substring(1).trim();
                if(message.equals("close")){
                    close();
                }
                if(message.equals("start")){
                    listen();
                }
                if(message.startsWith("setport")){
                    String portString = message.substring(7).trim();
                    int port = Integer.parseInt(portString);
                    setPort(port);
                }
                if(message.equals("getport")){
                    console.display("Port is: " + getPort());
                }
                else{
                    console.display("Command not recognized: " + message);
                }
            }
            else
            {
                console.display(message);
                sendToAllClients("SERVER MSG> " + message);
            }
        }
        catch (IOException e)
        {
            console.display("Malfunction when processing command");
            e.printStackTrace();
        }

    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted()
    {
        System.out.println
                ("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped()
    {
        System.out.println
                ("Server has stopped listening for connections.");
    }

    //Class methods ***************************************************

}
//End of EchoServer class
