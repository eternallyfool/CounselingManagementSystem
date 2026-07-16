/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.QueueNumber;
import java.util.ArrayList;
import java.util.List;

public class QueueNumberFileRepository {

    private final String filePath = "data/queue_numbers.txt";

    public List<QueueNumber> getAllQueueNumbers() {
        List<QueueNumber> queues = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            QueueNumber queue = parseQueueNumber(line);

            if (queue != null) {
                queues.add(queue);
            }
        }

        return queues;
    }

    public QueueNumber findByAppointmentId(String appointmentId) {
        for (QueueNumber queue : getAllQueueNumbers()) {
            if (queue.getAppointmentId().equals(appointmentId)) {
                return queue;
            }
        }

        return null;
    }

    public void addQueueNumber(QueueNumber queueNumber) {
        FileManager.appendLine(filePath, queueNumber.toDataString());
    }

    public boolean updateQueueNumber(QueueNumber updatedQueueNumber) {
        List<QueueNumber> queues = getAllQueueNumbers();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (QueueNumber queue : queues) {
            if (queue.getAppointmentId().equals(updatedQueueNumber.getAppointmentId())) {
                lines.add(updatedQueueNumber.toDataString());
                updated = true;
            } else {
                lines.add(queue.toDataString());
            }
        }

        if (updated) {
            FileManager.writeLines(filePath, lines);
        }

        return updated;
    }

    public String getNextQueueNo() {
        int highest = 0;

        for (QueueNumber queue : getAllQueueNumbers()) {
            try {
                int number = Integer.parseInt(queue.getQueueNo());

                if (number > highest) {
                    highest = number;
                }
            } catch (NumberFormatException e) {
                // Ignore malformed queue numbers.
            }
        }

        return String.format("%03d", highest + 1);
    }

    private QueueNumber parseQueueNumber(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 4) {
            return null;
        }

        return new QueueNumber(parts[0], parts[1], parts[2], parts[3]);
    }
}

