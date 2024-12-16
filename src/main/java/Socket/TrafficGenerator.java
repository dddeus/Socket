package Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TrafficGenerator {

    private String serverAddress;
    private int serverPort;
    private int packetSize;
    private int packetCount;
    private int interval;
    private long startTime;
    private long bytesSent;
    private TrafficStatistics stats;

    public TrafficGenerator(String serverAddress, int serverPort, int packetSize, int packetCount, int interval, TrafficStatistics stats) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.packetSize = packetSize;
        this.packetCount = packetCount;
        this.interval = interval;
        this.stats = stats;
        this.bytesSent = 0;
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverInetAddress = InetAddress.getByName(serverAddress);

            for (int i = 0; i < packetCount; i++) {
                byte[] packet = new byte[packetSize];
                long timestamp = System.currentTimeMillis();
                for (int j = 0; j < 8; j++) {
                    packet[j] = (byte) (timestamp >> (8 * (7 - j)) & 0xFF);
                }

                DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, serverInetAddress, serverPort);
                socket.send(datagramPacket);
                bytesSent += packet.length;

                stats.incrementSent(packet.length);

                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;

                if (elapsedTime > 0) {
                    double speed = (bytesSent / (double) elapsedTime) * 1000;
                    System.out.println("Sent speed: " + SpeedFormatter.formatSpeed((long) speed));
                }

                System.out.println("Packet sent\n");

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    System.err.println("TrafficGenerator interrupted: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("IOException in TrafficGenerator: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
