package com.lloseng.ocsf.simpleChat;

// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com
import com.lloseng.ocsf.client.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
    //Instance variables **********************************************
    ChatIF clientUI;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean heartbeatReceived = true;
        /**
         * The interface type variable.  It allows the implementation of
         * the display method in the client.
         */


    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException
    {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
        openConnection();


//        scheduler.scheduleAtFixedRate(() -> {
//            if (!heartbeatReceived) {
//                clientUI.display("Server not responding. Not terminating client, workaround active");
////                quit();
//            }
//            heartbeatReceived = false;
//            try {
//                sendToServer("heartbeat");
//            } catch (IOException e) {
//                clientUI.display("Error sending heartbeat.");
//            }
//        }, 5, 5, TimeUnit.SECONDS);  // Send heartbeat every 5 seconds
    }


    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg)
    {
        if(msg.equals("heartbeat")){
            heartbeatReceived = true;
        }
        else{
            clientUI.display(msg.toString());
        }
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message)
    {
        try
        {
            //todo: handle commands and call from AbstractClient
            if(message.startsWith("#")){ //Command
                message=message.substring(1).trim();
                if (message.equals("quit")){
                    closeConnection();
                    quit();
                }
                else if(message.equals("logoff")){
                    closeConnection();
                }
                else if(message.startsWith("sethost")){
                    setHost(message.substring(7).trim()); // MAY FAIL
                }
                else if(message.startsWith("setport")){
                    int port = Integer.parseInt(message.substring(7).trim());
                    setPort(port); // MAY FAIL
                }
                else if(message.equals("login")){
                    openConnection();
                    clientUI.display("Connection back up!");
                }
                else if(message.equals("gethost")){
                    clientUI.display(getHost());
                }
                else if(message.equals("getport")){
                    clientUI.display(String.valueOf(getPort()));
                }
                else {
                    clientUI.display("Command not recognized.");
                }
            }
            else
                sendToServer(message);
        }
        catch(IOException e)
        {
            clientUI.display("Could not send message to server. Connection closed");
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit()
    {
        try
        {
            closeConnection();
        }
        catch(IOException e) {}
        System.exit(0);
    }
}
//End of ChatClient class
