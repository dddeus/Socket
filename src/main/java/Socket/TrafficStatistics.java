package Socket;

/**
 * Класс для сбора статистики отправленных и полученных пакетов.
 */
public class TrafficStatistics {

    private long totalSentBytes;     // Общее количество отправленных байтов
    private long totalReceivedBytes; // Общее количество полученных байтов
    private long totalReceivedPackets; // Количество полученных пакетов
    private long totalReceivedDelay;  // Суммарная задержка полученных пакетов

    /**
     * Конструктор для инициализации статистики.
     */
    public TrafficStatistics() {
        this.totalSentBytes = 0;
        this.totalReceivedBytes = 0;
        this.totalReceivedPackets = 0;
        this.totalReceivedDelay = 0;
    }

    /**
     * Увеличивает количество отправленных байтов.
     *
     * @param bytes Количество байтов, которые были отправлены.
     */
    public synchronized void incrementSent(long bytes) {
        totalSentBytes += bytes;
    }

    /**
     * Увеличивает количество полученных байтов и добавляет задержку.
     *
     * @param bytes Количество байтов, которые были получены.
     * @param delay Задержка в миллисекундах для полученного пакета.
     */
    public synchronized void incrementReceived(long bytes, long delay) {
        totalReceivedBytes += bytes;
        totalReceivedPackets++;
        totalReceivedDelay += delay;
    }

    /**
     * Возвращает скорость отправки данных в байтах/сек.
     *
     * @param elapsedTimeMillis Время с момента начала отправки данных в миллисекундах.
     * @return Скорость отправки данных в байтах/сек.
     */
    public double getSendSpeed(long elapsedTimeMillis) {
        return (totalSentBytes / (double) elapsedTimeMillis) * 1000;
    }

    /**
     * Возвращает среднюю задержку полученных пакетов.
     *
     * @return Средняя задержка в миллисекундах.
     */
    public double getAverageDelay() {
        if (totalReceivedPackets > 0) {
            return totalReceivedDelay / (double) totalReceivedPackets;
        }
        return 0;
    }

    /**
     * Возвращает статистику в виде строки.
     *
     * @return Строка с информацией об отправленных и полученных данных.
     */
    public String getStatistics() {
        return String.format("Sent: %d bytes, Received: %d bytes, Average Delay: %.2f ms",
                totalSentBytes, totalReceivedBytes, getAverageDelay());
    }
}
