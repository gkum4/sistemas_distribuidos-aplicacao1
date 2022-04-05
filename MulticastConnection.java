import java.net.*;
import java.io.*;

public class MulticastConnection {
  private final int multicastPort = 6789;
  private InetAddress multicastGroup;
  private MulticastSocket multicastSocket;

  public MulticastConnection(String address) {
    setupMulticastGroup(address);
    setupMulticastSocket();
    enterMulticastGroup();
  }

  private void setupMulticastGroup(String address) {
    try {
      multicastGroup = InetAddress.getByName(address);
    } catch (UnknownHostException e) {
      System.out.println("Unable to setup InetAddress group: " + e.getMessage());
    }
  }

  private void setupMulticastSocket() {
    try {
      multicastSocket = new MulticastSocket(multicastPort);
    } catch (IOException e) {
      System.out.println("Unable to setup MulticastSocket: " + e.getMessage());
    }
  }

  private void enterMulticastGroup() {
    if (multicastSocket == null) {
      System.out.println("Error: need to setup multicastSocket first.");
      return;
    }

    try {
      multicastSocket.joinGroup(multicastGroup);
    } catch (IOException e) {
      System.out.println("Unable to enter MulticastSocket group: " + e.getMessage());
    }
  }

  public void sendMessage(String m) {
    if (multicastGroup == null || multicastSocket == null) {
      System.out.println("Error: need to setup multicastGroup and multicastSocket first.");
      return;
    }

    byte[] message = m.getBytes();
    DatagramPacket messageOut = new DatagramPacket(message, message.length, multicastGroup, multicastPort);

    try {
      multicastSocket.send(messageOut);
    } catch (IOException e) {
      System.out.println("Unable to send MulticastSocket message: " + e.getMessage());
    }
  }

  /**
   * Wait until receive message
  */
  public String listenToMessage() {
    if (multicastSocket == null) {
      System.out.println("Error: need to setup multicastSocket first.");
      return "";
    }

    try {
      byte[] buffer = new byte[1000];

      DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
      multicastSocket.receive(messageIn);

      return new String(messageIn.getData());
    } catch (IOException e) {
      System.out.println("Unable to receive MulticastSocket message: " + e.getMessage());
    }

    return "";
  }

  public void leaveMulticastGroup() {
    if (multicastSocket == null || multicastGroup == null) {
      System.out.println("Error: need to setup multicastSocket and multicastGroup first.");
      return;
    }

    try {
      multicastSocket.leaveGroup(multicastGroup);
    } catch (IOException e) {
      System.out.println("Unable to leave MulticastSocket group: " + e.getMessage());
    } finally {
      multicastSocket.close();
    }
  }
}
