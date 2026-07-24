/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.ReportSummary;
import cms.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class ReportFileRepository {

    private final String filePath = "data/reports.txt";

    public List<ReportSummary> getAllReports() {
        List<ReportSummary> reports = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            ReportSummary report = parseReport(line);

            if (report != null) {
                reports.add(report);
            }
        }

        return reports;
    }

    public void addReport(ReportSummary report) {
        FileManager.appendLine(filePath, report.toDataString());
    }

    public String getNextReportId() {
        List<String> ids = new ArrayList<>();

        for (ReportSummary report : getAllReports()) {
            ids.add(report.getReportId());
        }

        return IdGenerator.generateNextId("RP", ids);
    }

    private ReportSummary parseReport(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 11) {
            return null;
        }

        try {
            return new ReportSummary(parts[0], parts[1], parts[2], parts[3], parts[4],
                    parts[5], Integer.parseInt(parts[6]), Integer.parseInt(parts[7]),
                    Integer.parseInt(parts[8]), Integer.parseInt(parts[9]),
                    Integer.parseInt(parts[10]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
