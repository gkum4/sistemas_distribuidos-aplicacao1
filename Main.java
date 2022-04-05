import java.util.*;
import java.net.*;
import java.io.*;

public class Main {
  private static final int[] serverPorts = {7896, 7897, 7998};

  private static String processId;
  private static State state = State.RELEASED;
  private static CriticalSectionHandler criticalSectionHandler;
  private static List<String[]> waitingProcesses = new ArrayList<String[]>();
  private static long timeAskedToEnterCriticalSection;

  public static void main(String[] args) {
    // args give process Id (e.g. "1") and destination multicast group (e.g. "228.5.6.7")
    processId = args[0];
    String multicastGroupAddress = args[1];
    MulticastConnection multicastConnection = new MulticastConnection(multicastGroupAddress);

    criticalSectionHandler = new CriticalSectionHandler(
      multicastConnection,
      processId,
      serverPorts[Integer.parseInt(processId) - 1]
    );

    new Thread(inputReader).start();

    while (true) {
      String receivedMessage = multicastConnection.listenToMessage();
      String[] messageParts = receivedMessage.split(",");
      messageParts[0] = messageParts[0].replaceAll("[^\\d.]", "");
      messageParts[1] = messageParts[1].replaceAll("[^\\d.]", "");
      String senderProcessId = messageParts[1];

      if (senderProcessId.equals(processId)) {
        continue;
      }

      int serverPort = serverPorts[Integer.parseInt(senderProcessId) - 1];

      if (state == State.HELD || (state == State.WANTED && shouldEnterCriticalSectionFirst(messageParts))) {
        waitingProcesses.add(messageParts);
        continue;
      }

      TCPClientConnection tcpClientConnection = new TCPClientConnection("localhost", serverPort);
      tcpClientConnection.sendMessage("OK");
      tcpClientConnection.closeConnection();
    }
  }

  private static boolean shouldEnterCriticalSectionFirst(String[] messageParts) {
    long otherProcessTimeAsked = Long.parseLong(messageParts[0]);

    if (timeAskedToEnterCriticalSection == otherProcessTimeAsked) {
      String otherProcessId = messageParts[1];

      return Integer.parseInt(processId) < Integer.parseInt(otherProcessId);
    }

    return timeAskedToEnterCriticalSection < otherProcessTimeAsked;
  }

  private static final Runnable inputReader = new Runnable() {
    public void run() {
      Scanner scanner = new Scanner(System.in);
      printInstructions();

      while (true) {
        String userInput = scanner.nextLine();
        System.out.println("");

        if (userInput.equals("1")) {
          if (state == State.WANTED || state == State.HELD) {
            System.out.println("Você já está na Seção Crítica, tente outro comando.");
            continue;
          }

          state = State.WANTED;
          timeAskedToEnterCriticalSection = System.currentTimeMillis();
          criticalSectionHandler.enter(timeAskedToEnterCriticalSection);
          state = State.HELD;

          System.out.println("Você ENTROU na Seção Crítica! Digite \"2\" para sair.");
          continue;
        }

        if (userInput.equals("2")) {
          if (state != State.HELD) {
            System.out.println("Você não está na Seção Crítica, tente outro comando.");
            continue;
          }

          state = State.RELEASED;
          criticalSectionHandler.leave(waitingProcesses, serverPorts);
          waitingProcesses = new ArrayList<String[]>();

          System.out.println("Você SAIU da Seção Crítica! Digite \"1\" para entrar novamente.");
          continue;
        }

        System.out.println("Comando inválido.\n");
        printInstructions();
      }
    }

    private void printInstructions() {
      System.out.println("Seja bem vindo à Aplicação 1 - Sockets!");
      System.out.println("Digite:");
      System.out.println("\"1\" para entrar na Seção Crítica.");
      System.out.println("\"2\" para sair da Seção Crítica.");
    }
  };
}