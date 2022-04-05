import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClientConnection {
  private Socket socket;
  private DataOutputStream outputStream;

  public TCPClientConnection(String hostname, int serverPort) {
    setupSocket(hostname, serverPort);
    setupDataOutputStream();
  }

  private void setupSocket(String hostname, int serverPort) {
    try {
      socket = new Socket(hostname, serverPort);
    } catch (UnknownHostException e) {
      System.out.println("Unable to setup socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("readline: " + e.getMessage());
    }
  }

  private void setupDataOutputStream() {
    if (socket == null) {
      System.out.println("Error: need to setup socket first.");
      return;
    }

    try {
      outputStream = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      System.out.println("readline: " + e.getMessage());
    }
  }

  public void sendMessage(String message) {
    if (outputStream == null) {
      System.out.println("Error: need to setup outputStream first.");
      return;
    }

    try {
      outputStream.writeUTF(message);
    } catch (IOException e) {
      System.out.println("readline: " + e.getMessage());
    }
  }

  public void closeConnection() {
    if (socket == null) {
      System.out.println("Error: need to setup socket first.");
      return;
    }

    try {
      socket.close();
    } catch (IOException e) {
      System.out.println("Unable to close connection: " + e.getMessage());
    }
  }
}
