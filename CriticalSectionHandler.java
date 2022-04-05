import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Objects;

public class CriticalSectionHandler {
  MulticastConnection multicastConnection;
  String processId;
  int serverPort;
  private int numberOfProcesses = 3;

  public CriticalSectionHandler(MulticastConnection multicastConnection, String processId, int serverPort) {
    this.multicastConnection = multicastConnection;
    this.processId = processId;
    this.serverPort = serverPort;
  }

  public void enter(long timeAskedToEnterCriticalSection) {
    TCPServerConnection tcpServerConnection = new TCPServerConnection(serverPort);

    String message = timeAskedToEnterCriticalSection + "," + processId;
    multicastConnection.sendMessage(message);

    int messagesReceived = 0;

    while (messagesReceived < numberOfProcesses-1) {
      String messageReceived = tcpServerConnection.listenToMessage();

      if (Objects.equals(messageReceived, "OK")) {
        messagesReceived += 1;
      }
    }

    tcpServerConnection.closeConnection();
  }

  public void leave(List<String[]> waitingProcesses, int[] serverPorts) {
    for (String[] waitingProcess : waitingProcesses) {
      String processId = waitingProcess[1];
      int serverPort = serverPorts[Integer.parseInt(processId) - 1];

      TCPClientConnection tcpClientConnection = new TCPClientConnection("localhost", serverPort);
      tcpClientConnection.sendMessage("OK");
      tcpClientConnection.closeConnection();
    }
  }
}
