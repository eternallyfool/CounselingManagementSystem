/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.ConsultationRecord;
import cms.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class ConsultationFileRepository {

    private final String filePath = "data/consultation_records.txt";

    public List<ConsultationRecord> getAllRecords() {
        List<ConsultationRecord> records = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            ConsultationRecord record = parseRecord(line);

            if (record != null) {
                records.add(record);
            }
        }

        return records;
    }

    public ConsultationRecord findById(String recordId) {
        for (ConsultationRecord record : getAllRecords()) {
            if (record.getRecordId().equals(recordId)) {
                return record;
            }
        }

        return null;
    }

    public ConsultationRecord findByAppointmentId(String appointmentId) {
        for (ConsultationRecord record : getAllRecords()) {
            if (record.getAppointmentId().equals(appointmentId)) {
                return record;
            }
        }

        return null;
    }

    public void addRecord(ConsultationRecord record) {
        FileManager.appendLine(filePath, record.toDataString());
    }

    public boolean updateRecord(ConsultationRecord updatedRecord) {
        List<ConsultationRecord> records = getAllRecords();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (ConsultationRecord record : records) {
            if (record.getRecordId().equals(updatedRecord.getRecordId())) {
                lines.add(updatedRecord.toDataString());
                updated = true;
            } else {
                lines.add(record.toDataString());
            }
        }

        if (updated) {
            FileManager.writeLines(filePath, lines);
        }

        return updated;
    }

    public String getNextRecordId() {
        List<String> ids = new ArrayList<>();

        for (ConsultationRecord record : getAllRecords()) {
            ids.add(record.getRecordId());
        }

        return IdGenerator.generateNextId("C", ids);
    }

    private ConsultationRecord parseRecord(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 5) {
            return null;
        }

        return new ConsultationRecord(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}
