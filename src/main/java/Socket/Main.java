package Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        logger.info("Application started.");

        System.out.println("Select mode of operation:");
        System.out.println("1 - Send packets to external server");
        System.out.println("2 - Send packets to yourself (loopback)");
        int mode;

        while (true) {
            System.out.print("Enter mode (1 or 2): ");
            mode = scanner.nextInt();
            if (mode == 1 || mode == 2) {
                logger.info("Mode selected: {}", mode);
                break;
            } else {
                logger.warn("Invalid mode entered.");
                System.out.println("Error: Invalid mode. Please enter 1 or 2.");
            }
        }

        String serverAddress;
        if (mode == 2) {
            serverAddress = "127.0.0.1"; // Локальный адрес для отправки самому себе
            logger.info("Loopback mode selected: packets will be sent to 127.0.0.1.");
        } else {
            scanner.nextLine(); // Очистка буфера после ввода числа
            while (true) {
                System.out.print("Enter server address: ");
                serverAddress = scanner.nextLine();

                if (serverAddress.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                    logger.info("Server address set to: {}", serverAddress);
                    break;
                } else {
                    logger.warn("Invalid server address entered.");
                    System.out.println("Error: Invalid IP address format. Please try again.");
                }
            }
        }

        int serverPort;
        while (true) {
            System.out.print("Enter server port: ");
            String portInput = scanner.next();

            if (!portInput.matches("\\d+")) {
                logger.warn("Non-numeric port input detected.");
                System.out.println("Error: Port must contain only digits. Please try again.");
                continue;
            }

            serverPort = Integer.parseInt(portInput);
            if (serverPort >= 1 && serverPort <= 65535) {
                logger.info("Server port set to: {}", serverPort);
                break;
            } else {
                logger.warn("Invalid port entered: {}", serverPort);
                System.out.println("Error: Invalid port. Port must be in the range 1 to 65535. Please try again.");
            }
        }

        System.out.print("Enter packet size (bytes): ");
        int packetSize = scanner.nextInt();
        logger.info("Packet size set to: {} bytes", packetSize);

        System.out.print("Enter number of packets: ");
        int packetCount = scanner.nextInt();
        logger.info("Number of packets set to: {}", packetCount);

        System.out.print("Enter interval between packets (ms): ");
        int interval = scanner.nextInt();
        logger.info("Interval set to: {} ms", interval);

        TrafficStatistics stats = new TrafficStatistics();

        TrafficGenerator generator = new TrafficGenerator(serverAddress, serverPort, packetSize, packetCount, interval, stats);
        TrafficReceiver receiver = new TrafficReceiver(serverPort, packetSize, stats);

        // В режиме 2 автоматически запускаем приемник на localhost
        if (mode == 2) {
            Thread receiverThread = new Thread(() -> {
                logger.info("Starting TrafficReceiver on localhost...");
                receiver.start();
            });
            receiverThread.start();
        }

        Thread generatorThread = new Thread(() -> {
            logger.info("Starting TrafficGenerator...");
            generator.start();
        });

        generatorThread.start();

        try {
            generatorThread.join();
            logger.info("TrafficGenerator has finished execution.");
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted: ", e);
            e.printStackTrace();
        }

        logger.info("Application finished.");
    }
}
