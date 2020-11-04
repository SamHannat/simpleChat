// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

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
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;

  /**
   * Keeps track of login
   */
  String loginid;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginid, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginid = loginid;
    openConnection();
    this.sendToServer("#login <" + loginid + ">");
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {

    if(message.strip().charAt(0) == '#') {
      String tmp;
      message = message.strip();
      switch(message) {
        case "#quit":
          quit();
        case "#logoff":
          try {
            closeConnection();
          } catch (IOException e) {
            System.out.println("An error occurred while trying to disconnect from the server.");
          }
          break;
        case "#login":
          // do the thing
          if(isConnected()) {
            System.out.println("Already connected to a client.");
          } else {
            try {
              openConnection();
              System.out.println("Connected to client.");
            } catch (IOException e) {
              System.out.println("Unable to connect to client " + getHost() + ":" + getPort());
            }
          }
          break;
        case "#gethost":
          System.out.println(getHost());
          break;
          case "#getport":
          System.out.println(getPort());
          break;
        default:
          if (message.startsWith("#sethost")) {

            try {
              tmp = message.split(" ")[1];
              setHost(tmp);
              System.out.println("Host set to: " + tmp);
            } catch (Exception e) {
              System.out.println("Invalid host.");
            }

          } else if (message.startsWith("#setport")) {
            try {
              int temp = Integer.parseInt(message.split(" ")[1]);
              setPort(temp);
              System.out.println("port set to: " + temp);
            } catch (Exception e) {
              System.out.println("Invalid port.");
            }

          } else {
            System.out.println("Unknown command.");
          }
      }
    } else {
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
                ("Could not send message to server.  Terminating client.");
        quit();
      }
    }


  }

  /**
   * This method handles the server getting shut down.
   */
  @Override
  public void connectionClosed() {
    clientUI.display("Your connection to the server has closed.");
  }

  @Override
  protected void connectionException(Exception exception) {
    System.out.println("Server has disconnected. Disconnecting...");
    quit();
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
