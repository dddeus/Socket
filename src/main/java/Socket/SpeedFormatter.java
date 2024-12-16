package Socket;

/**
 * Класс для форматирования скорости передачи данных.
 */
public class SpeedFormatter {

    /**
     * Форматирует скорость передачи из байтов/сек в удобночитаемый формат.
     *
     * @param bytesPerSecond Скорость передачи в байтах/сек.
     * @return Строка с отформатированной скоростью передачи данных.
     */
    public static String formatSpeed(long bytesPerSecond) {
        double speed = bytesPerSecond;
        String[] units = {"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
        int unitIndex = 0;

        while (speed >= 1024 && unitIndex < units.length - 1) {
            speed /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", speed, units[unitIndex]);
    }
}
