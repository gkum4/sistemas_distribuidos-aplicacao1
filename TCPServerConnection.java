import java.net.*;
import java.io.*;

public class TCPServerConnection {
  private ServerSocket serverSocket;

  public TCPServerConnection(int serverPort) {
    setupServerSocket(serverPort);
  }

  private void setupServerSocket(int serverPort) {
    try {
      serverSocket = new ServerSocket(serverPort);
    } catch(IOException e) {
      System.out.println("Unable to setup serverSocket: " + e.getMessage());
    }
  }

  public String listenToMessage() {
    try {
      Socket clientSocket = serverSocket.accept();
      DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
      String message = inputStream.readUTF();

      clientSocket.close();

      return message;
    } catch (IOException e) {
      System.out.println("Unable to listen to messages from clientSocket: " + e.getMessage());
    }

    return "";
  }

  public void closeConnection() {
    if (serverSocket == null) {
      System.out.println("Error: setup serverSocket first.");
      return;
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      System.out.println("Unable to close serverSocket: " + e.getMessage());
    }
  }
}