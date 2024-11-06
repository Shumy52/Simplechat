package com.lloseng.ocsf.tests;

import java.net.*;
import java.io.*;
import com.lloseng.ocsf.server.*;
import com.lloseng.ocsf.client.*;

/**

<b>Testing the connection factory.</b>
Server will listen. A connection is created without factory.
Then, a connection is created with a factory. The handling
message returns true the first time and false the second time.<p>

<b>Instructions</b>: Runs this testcase byt typing <code>java Testcase4</code>. 
The classpath must point to the <code>com</code> directory of OCSF and
to the directory that contains the testcase. Note that sleeping delay has 
been added between each block of tests in order to avoid race issues
between threads.<p>

<b>Expected results</b>: The following output should be obtained. The order inside
each block can change.
 <blockquote>
 <pre>
*** Testing the connection factory ***

-- Server listens --
Server: Started


-- Client connects --
Server: client #1 connected
Client: Connected


-- Client sends message --
Server: Message received=Message handled by server only


-- Client disconnects --
Connection to client exception: java.io.EOFException
Server: client disconnected
Client: Closed


-- Sets the factory --


-- Client connects --
ConnectionToClient: Constructed
Server: client #1 connected
Client: Connected


-- Client sends message --
ConnectionToClient: Message received=Message handled by connection and server
Server: Message received=Message handled by connection and server


-- Client sends message --
ConnectionToClient: Message received=Message handled by connection only


-- Server closes --
Client exception: java.io.EOFException
Client: Closed
Server: Stopped
Server: Closed
Server: client disconnected
 </pre>
 </blockquote>
 
**/

public class Testcase4 extends Thread {

  public static void main(String[] args) {
    
    System.out.println("*** Testing the connection factory ***\n");

    Thread test= new Testcase4();
    test.start();
  }

  Server server;
  Client client;
  
  public Testcase4() {
  
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
     client.sendToServer("Message handled by server only");
     Thread.sleep(1000);
     System.out.println("\n");

     // Client disconnects
     System.out.println("-- Client disconnects --");
     client.closeConnection();
     Thread.sleep(1000);
     System.out.println("\n");

     // Sets the factory
     System.out.println("-- Sets the factory --");
     server.setConnectionFactory(new ConnectionFactory());
     System.out.println("\n");
          
     // Client connects
     System.out.println("-- Client connects --");
     client.openConnection();
     Thread.sleep(1000);
     System.out.println("\n");

     // Client sends message
     System.out.println("-- Client sends message --");
     client.sendToServer("Message handled by connection and server");
     Thread.sleep(1000);
     System.out.println("\n");
          
     // Client sends message
     System.out.println("-- Client sends message --");
     client.sendToServer("Message handled by connection only");
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
  }
}

class Connection extends ConnectionToClient {

  protected Connection(ThreadGroup group, Socket clientSocket,
    AbstractServer server) throws IOException {
    
    super(group,clientSocket,server);
    System.out.println("ConnectionToClient: Constructed");
  }
    
    
  protected boolean handleMessageFromClient(Object msg)
  {
    System.out.println("ConnectionToClient: Message received=" + msg);    

    if (msg.toString().indexOf("only")>=0) 
      return false;
    else 
      return true;
  }
}

class ConnectionFactory extends AbstractConnectionFactory
{
  protected ConnectionToClient createConnection(ThreadGroup group, 
     Socket clientSocket, AbstractServer server) throws IOException {
     
     return new Connection(group,clientSocket,server);
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
  }
}  
}
