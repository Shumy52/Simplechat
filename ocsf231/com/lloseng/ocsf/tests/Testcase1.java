package com.lloseng.ocsf.tests;

import com.lloseng.ocsf.server.*;
import com.lloseng.ocsf.client.*;

/**

<b>Testing of basic OCSF client/server functionnalities.</b>
Server will listen, stop, send message, 
close client, listen again and close.
Clients will connect, send messages, and close.<p>

<b>Instructions</b>: Runs this testcase byt typing <code>java Testcase1</code>. 
The classpath must point to the <code>com</code> directory of OCSF and
to the directory that contains the testcase. Note that sleeping delay has 
been added between each block of tests in order to avoid race issues
between threads.<p>

<b>Expected results</b>: The following output should be obtained. The order inside
each block can change. Some notes have also been added to this output.
 <blockquote>
 <pre>
*** Basic tests ***

-- Server listens --
Server.getPort()=12345                          # Server created
Server.isListening()=false
Server: Started
Server.isListening()=true                       # Now server is listening
Server.isClosed()=false
Server.getNumberOfClients()=0


-- Client 1 connects and sends message --
Client.isConnected()=false                      # Client created
Server: client #1 connected
Client.getInetAddress()=localhost/127.0.0.1
Client: Connected                               # Now client is connected
Client.isConnected()=true
Server: Message received=Test message A


-- Client 2 connects and sends message --
Server: client #2 connected
Client: Connected
Client.isConnected()=true
Server: Message received=Test message B


-- Server sends message --
i)Server.getNumberOfClients()=2
Client: Message received=Test message C        # Message received by the 2 clients
Client: Message received=Test message C


-- Client 2 disconnects --
Client: Closed
Client.isConnected()=false
Connection to client exception: java.io.EOFException 
Server: client disconnected

-- Server stops and sends message --
ii)Server.getNumberOfClients()=1               # Now at 1
Client: Message received=Test message D
Server: Stopped
Server.isListening()=false
Server.isClosed()=false
Server.getNumberOfClients()=1                  # Still 1 client must be connected


-- Server close connection --
Client exception: java.io.EOFException         # Server kills that client
Client: Closed
Client.isConnected()=false
Server: client disconnected


-- Server listens, client connects --
iii)Server.getNumberOfClients()=0              # Now at 0
Server: Started
Server.isListening()=true
Server.isClosed()=false
Server.getNumberOfClients()=0
Server: client #1 connected
Client: Connected
Client.isConnected()=true


-- Server closes --
iv)Server.getNumberOfClients()=1
Server: client disconnected                   # All client are disconnected
Client exception: java.io.EOFException        # Because server has closed
Server: Stopped                               # Server stops before closing
Client: Closed
Server.isListening()=false
Client.isConnected()=false
Server.isClosed()=true
Server.getNumberOfClients()=0
Server: Closed
Server.isListening()=false
Server.isClosed()=true
Server.getNumberOfClients()=0
 </pre>
 </blockquote>
 
**/

public class Testcase1 extends Thread {

  public static void main(String[] args) {
    
    System.out.println("*** Basic tests ***\n");

    Thread test= new Testcase1();
    test.start();
  }

  Server server;
  Client client1;
  Client client2;
  
  public Testcase1() {
  
    server= new Server(12345);
    client1= new Client("localhost",12345);  
    client2= new Client("localhost",12345);  
  }

  public void run() {
  
    try {

     // Server listens    
     System.out.println("-- Server listens --");
     System.out.println("Server.getPort()="+server.getPort());
     System.out.println("Server.isListening()="+server.isListening());
     server.listen();
     Thread.sleep(1000);
     System.out.println("\n");
    
     // Client 1 connects and sends message
     System.out.println("-- Client 1 connects and sends message --");
     System.out.println("Client.isConnected()="+client1.isConnected());
     client1.openConnection();
     System.out.println("Client.getInetAddress()="+client1.getInetAddress());
     client1.sendToServer("Test message A");
     Thread.sleep(1000);
     System.out.println("\n");

     // Client 1 connects and sends message
     System.out.println("-- Client 2 connects and sends message --");
     client2.openConnection();
     client2.sendToServer("Test message B");
     Thread.sleep(1000);
     System.out.println("\n");

     // Server sends message
     System.out.println("-- Server sends message --");
     System.out.println("i)Server.getNumberOfClients()="+server.getNumberOfClients());
     server.sendToAllClients("Test message C");
     Thread.sleep(1000);
     System.out.println("\n");

     // Client 2 disconnects
     System.out.println("-- Client 2 disconnects --");
     client2.closeConnection();
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server stops and sends message
     System.out.println("-- Server stops and sends message --");
     server.stopListening();
     System.out.println("ii)Server.getNumberOfClients()="+server.getNumberOfClients());
     server.sendToAllClients("Test message D");
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server close connection
     System.out.println("-- Server close connection --");
     Thread[] ts= server.getClientConnections();
     ConnectionToClient cc= (ConnectionToClient)ts[0];
     cc.close();
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server listens, client connects
     System.out.println("-- Server listens, client connects --");
     System.out.println("iii)Server.getNumberOfClients()="+server.getNumberOfClients());
     server.listen();
     client2.openConnection();
     Thread.sleep(1000);
     System.out.println("\n");
     
     // Server closes
     System.out.println("-- Server closes --");
     System.out.println("iv)Server.getNumberOfClients()="+server.getNumberOfClients());
     server.close();
     
    } catch (Exception ex) {
    
      System.out.println("Server exception: " + ex);
    }
  }


private class Server extends AbstractServer {

  Server(int p) {
  
    super(p);
  }
  
  protected void serverStarted() {

    System.out.println("Server: Started");
    System.out.println("Server.isListening()="+isListening());
    System.out.println("Server.isClosed()="+isClosed());
    System.out.println("Server.getNumberOfClients()="+getNumberOfClients());
  }
  
  protected void serverStopped() {
  
    System.out.println("Server: Stopped");
    System.out.println("Server.isListening()="+isListening());
    System.out.println("Server.isClosed()="+isClosed());
    System.out.println("Server.getNumberOfClients()="+getNumberOfClients());
  }

  protected void serverClosed() {
  
    System.out.println("Server: Closed");
    System.out.println("Server.isListening()="+isListening());
    System.out.println("Server.isClosed()="+isClosed());
    System.out.println("Server.getNumberOfClients()="+getNumberOfClients());
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
  
private class Client extends AbstractClient {

  Client(String h, int p) {
  
    super(h,p);
  }
  
  protected void connectionClosed() {

    System.out.println("Client: Closed");
    System.out.println("Client.isConnected()="+isConnected());
  }
  
  protected void connectionException(Exception exception) {
  
    System.out.println("Client exception: " + exception);
  }
  
  protected void connectionEstablished() {

    System.out.println("Client: Connected");
    System.out.println("Client.isConnected()="+isConnected());
  }

  protected void handleMessageFromServer(Object msg){

    System.out.println("Client: Message received=" +msg);
  }
}
}
