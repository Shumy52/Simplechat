package com.lloseng.ocsf.tests;

import com.lloseng.ocsf.server.*;
import com.lloseng.ocsf.client.*;

/**

<b>Testing of exception throwing inside message handling.</b>
Server will listen, client will connect. Both will send messages
some of them causing the message handling method to send
an exception.<p>

<b>Instructions</b>: Runs this testcase byt typing <code>java Testcase3</code>. 
The classpath must point to the <code>com</code> directory of OCSF and
to the directory that contains the testcase. Note that sleeping delay has 
been added between each block of tests in order to avoid race issues
between threads.<p>

<b>Expected results</b>: The following output should be obtained. The order inside
each block can change.
 <blockquote>
 <pre>
*** Testing of exception throwing inside message handling ***

-- Server listens --
Server: Started


-- Client connects --
Server: client #1 connected
Client: Connected


-- Client sends message --
Server: Message received=Test message A


-- Client sends message (exception) --
Server: Message received=Message that should cause an exception
Connection to client exception: java.lang.NullPointerException: Error in message handling (server)


-- Client sends message --
Server: Message received=Test message B


-- Server sends message --
Client: Message received=Test message C


-- Server sends message (exception) --
Client: Message received=Message that should cause an exception
Client exception: java.lang.NullPointerException: Error in message handling (client)


-- Server sends message --
Client: Message received=Test message D


-- Server closes --
Client exception: java.io.EOFException
Client: Closed
Server: client disconnected
Server: Stopped
Server: Closed
 </pre>
 </blockquote>
 
**/

public class Testcase3 extends Thread {

  public static void main(String[] args) {
    
    System.out.println("*** Testing of exception throwing inside message handling ***\n");

    Thread test= new Testcase3();
    test.start();
  }

  Server server;
  Client client;
  
  public Testcase3() {
  
    server= new Server(12345);
    client= new Client("localhost",12345);  
  }

  public void run() {
  
    try {

     // Server listens    
     System.out.println("-- Server listens --");
     server.listen();
     Thread.sleep(1000);
     System.out.println("\n");
    
     // Client connects
     System.out.println("-- Client connects --");
     client.openConnection();
     Thread.sleep(1000);
     System.out.println("\n");

     // Client sends message
     System.out.println("-- Client sends message --");
     client.sendToServer("Test message A");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Client sends message
     System.out.println("-- Client sends message (exception) --");
     client.sendToServer("Message that should cause an exception");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Client sends message
     System.out.println("-- Client sends message --");
     client.sendToServer("Test message B");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server sends message
     System.out.println("-- Server sends message --");
     server.sendToAllClients("Test message C");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server sends message
     System.out.println("-- Server sends message (exception) --");
     server.sendToAllClients("Message that should cause an exception");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server sends message
     System.out.println("-- Server sends message --");
     server.sendToAllClients("Test message D");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server closes
     System.out.println("-- Server closes --");
     server.close();
     
    } catch (Exception ex) {
    
      System.out.println("Server exception: " + ex);
    }
  }

class Server extends AbstractServer {

  Server(int p) {
  
    super(p);
  }
  
  protected void serverStarted() {

    System.out.println("Server: Started");
  }
  
  protected void serverStopped() {
  
    System.out.println("Server: Stopped");
  }

  protected void serverClosed() {
  
    System.out.println("Server: Closed");
  }
  
  protected void clientConnected(ConnectionToClient client) {
  
    System.out.println("Server: client #" + getNumberOfClients() + " connected");
  }

  synchronized protected void clientDisconnected(
    ConnectionToClient client) {  

    System.out.println("Server: client disconnected");    
  }

  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
  
    System.out.println("Connection to client exception: " + exception);    
  }
  
  protected void listeningException(Throwable exception) {
  
    System.out.println("Listening exception: " + exception);    
  }
  
  protected void handleMessageFromClient(Object msg,
                               ConnectionToClient client){
                               
    System.out.println("Server: Message received=" + msg);
    
    if (msg.toString().indexOf("exception")>=0) {
    
      throw new NullPointerException("Error in message handling (server)");
    }
  }
}
  
class Client extends AbstractClient {

  Client(String h, int p) {
  
    super(h,p);
  }
  
  protected void connectionClosed() {

    System.out.println("Client: Closed");
  }
  
  protected void connectionException(Exception exception) {
  
    System.out.println("Client exception: " + exception);
  }
  
  protected void connectionEstablished() {

    System.out.println("Client: Connected");
  }

  protected void handleMessageFromServer(Object msg){

    System.out.println("Client: Message received=" +msg);
    
    if (msg.toString().indexOf("exception")>=0) {
    
      throw new NullPointerException("Error in message handling (client)");
    }
  }
}  
}
