package com.lloseng.ocsf.tests;

import com.lloseng.ocsf.server.*;
import com.lloseng.ocsf.client.*;

/**

<b>Testing the server backlog.</b>
Server backlog is set to 4. Server will listen, accept one client and stop. 
Five clients then try to connect, four should be put in backlog, the fifth
one should see its request refused. When back to listen the four waiting clients
will be accepted.<p>

<b>Instructions</b>: Runs this testcase byt typing <code>java Testcase2</code>. 
The classpath must point to the <code>com</code> directory of OCSF and
to the directory that contains the testcase. Note that sleeping delay has 
been added between each block of tests in order to avoid race issues
between threads.<p>

<b>Expected results</b>: The following output should be obtained. 
The order inside each block can change.
 <blockquote>
 <pre>
*** Backlog testing ***

-- Server listens with backlog = 4 --
Server: Started


-- Client 1 connects --
Client: Connected
Server: client #1 connected


-- Server stops --
Server: Stopped


-- Client 2 connects --


-- Client 3 connects --


-- Client 4 connects --


-- Client 5 connects --


-- Client 6 try to connects --
Server exception: java.net.ConnectException: Connection refused: connect


-- Server listens --
Server: Started
Client: Connected
Server: client #3 connected
Client: Connected
Server: client #4 connected
Client: Connected
Server: client #5 connected
Client: Connected
Server: client #5 connected


Server.getNumberOfClients()=5


-- Client 7 connects --
Client: Connected
Server: client #6 connected


-- Server closes --
Client exception: java.io.EOFException
Client: Closed
Client exception: java.io.EOFException
Client: Closed
Client exception: java.io.EOFException
Client: Closed
Client exception: java.io.EOFException
Client: Closed
Client exception: java.io.EOFException
Client: Closed
Server: client disconnected
Server: client disconnected
Server: client disconnected
Server: client disconnected
Server: client disconnected
Server: client disconnected
Client exception: java.io.EOFException
Client: Closed
Server: Stopped
Server: Closed
 </pre>
 </blockquote>
**/

public class Testcase2 extends Thread {

  public static void main(String[] args) {
    
    System.out.println("*** Backlog testing ***\n");

    Thread test= new Testcase2();
    test.start();
  }

  Server server;
  Client client1;
  Client client2;
  Client client3;
  Client client4;
  Client client5;
  Client client6;
  Client client7;
  
  public Testcase2() {
  
    server= new Server(12345);
    client1= new Client("localhost",12345);  
    client2= new Client("localhost",12345);  
    client3= new Client("localhost",12345);  
    client4= new Client("localhost",12345);  
    client5= new Client("localhost",12345);  
    client6= new Client("localhost",12345);  
    client7= new Client("localhost",12345);  
  }

  public void run() {
  
    try {

     // Server listens    
     System.out.println("-- Server listens with backlog = 4 --");
     server.setBacklog(4);
     server.listen();
     Thread.sleep(1000);
     System.out.println("\n");
    
     // Client 1 connects
     System.out.println("-- Client 1 connects --");
     (new ClientThread(client1)).start();
     Thread.sleep(500);
     System.out.println("\n");

     // Server stops   
     System.out.println("-- Server stops --");
     server.stopListening();
     Thread.sleep(1000);
     System.out.println("\n");

     // Client 2 connects
     System.out.println("-- Client 2 connects --");
     (new ClientThread(client2)).start();
     Thread.sleep(500);
     System.out.println("\n");

     // Client 3 connects
     System.out.println("-- Client 3 connects --");
     (new ClientThread(client3)).start();
     Thread.sleep(500);
     System.out.println("\n");

     // Client 4 connects
     System.out.println("-- Client 4 connects --");
     (new ClientThread(client4)).start();
     Thread.sleep(500);
     System.out.println("\n");

     // Client 5 connects
     System.out.println("-- Client 5 connects --");
     (new ClientThread(client5)).start();
     Thread.sleep(500);
     System.out.println("\n");
   
     // Client 6 try to connects
     System.out.println("-- Client 6 try to connects --");
     (new ClientThread(client6)).start();
     Thread.sleep(5000);
     System.out.println("\n");
   
     // Server listens    
     System.out.println("-- Server listens --");
     server.listen();
     Thread.sleep(500);
     System.out.println("\n");
     System.out.println("Server.getNumberOfClients()="+server.getNumberOfClients());
     Thread.sleep(500);
     System.out.println("\n");

     // Client 6 connects
     System.out.println("-- Client 7 connects --");
     (new ClientThread(client7)).start();
     Thread.sleep(500);
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

class ClientThread extends Thread {

  Client client;
  
  public ClientThread(Client client) {
  
    this.client= client;  
  }

  public void run() {

    try {
      client.openConnection();  
    } catch (Exception ex) {
    
      System.out.println("Server exception: " + ex);
    }
  }
}  
}
