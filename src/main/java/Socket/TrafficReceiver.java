package Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class TrafficReceiver {

    private int listenPort;
    private long startTime;
    private long bytesReceived;
    private int packetSize;
    private TrafficStatistics stats;

    public TrafficReceiver(int listenPort, int packetSize, TrafficStatistics stats) {
        this.listenPort = listenPort;
        this.packetSize = packetSize;
        this.bytesReceived = 0;
        this.stats = stats;
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(listenPort)) {
            byte[] buffer = new byte[packetSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                long currentTime = System.currentTimeMillis();

                long timestamp = 0;
                for (int i = 0; i < 8; i++) {
                    timestamp |= ((long) (packet.getData()[i] & 0xFF)) << (8 * (7 - i));
                }

                long delay = currentTime - timestamp;
                bytesReceived += packet.getLength();

                stats.incrementReceived(packet.getLength(), delay);

                long elapsedTime = currentTime - startTime;
                if (elapsedTime > 0) {
                    double speed = (bytesReceived / (double) elapsedTime) * 1000;
                    System.out.println("Received speed: " + SpeedFormatter.formatSpeed((long) speed));
                }

                System.out.println("Packet received. Delay: " + delay + " ms\n");
            }
        } catch (IOException e) {
            System.err.println("IOException in TrafficReceiver: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
