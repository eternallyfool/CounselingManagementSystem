/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import java.time.LocalDate;
import java.util.List;

public class ReportSummary {

    private String reportId;
    private String reportType;
    private String periodStart;
    private String periodEnd;
    private String generatedBy;
    private String generatedAt;
    private int totalAppointments;
    private int completed;
    private int cancelled;
    private int walkIn;
    private int online;

    public ReportSummary(String reportId, String reportType, String periodStart, String periodEnd,
            String generatedBy, String generatedAt, int totalAppointments, int completed,
            int cancelled, int walkIn, int online) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
        this.totalAppointments = totalAppointments;
        this.completed = completed;
        this.cancelled = cancelled;
        this.walkIn = walkIn;
        this.online = online;
    }

    public static ReportSummary generate(String reportId, String reportType, String periodStart,
            String periodEnd, String generatedBy, String generatedAt, List<Appointment> appointments) {
        int total = 0;
        int completed = 0;
        int cancelled = 0;
        int walkIn = 0;
        int online = 0;

        LocalDate start = LocalDate.parse(periodStart);
        LocalDate end = LocalDate.parse(periodEnd);

        for (Appointment appointment : appointments) {
            LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate());

            if (!appointmentDate.isBefore(start) && !appointmentDate.isAfter(end)) {
                total++;

                if (Appointment.STATUS_COMPLETED.equals(appointment.getStatus())) {
                    completed++;
                }

                if (Appointment.STATUS_CANCELLED.equals(appointment.getStatus())) {
                    cancelled++;
                }

                if (Appointment.TYPE_WALK_IN.equals(appointment.getBookingType())) {
                    walkIn++;
                }

                if (Appointment.TYPE_ONLINE.equals(appointment.getBookingType())) {
                    online++;
                }
            }
        }

        return new ReportSummary(reportId, reportType, periodStart, periodEnd, generatedBy,
                generatedAt, total, completed, cancelled, walkIn, online);
    }

    public String getReportId() {
        return reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getCompleted() {
        return completed;
    }

    public int getCancelled() {
        return cancelled;
    }

    public int getWalkIn() {
        return walkIn;
    }

    public int getOnline() {
        return online;
    }

    public String toDataString() {
        return reportId + "|" + reportType + "|" + periodStart + "|" + periodEnd + "|"
                + generatedBy + "|" + generatedAt + "|" + totalAppointments + "|"
                + completed + "|" + cancelled + "|" + walkIn + "|" + online;
    }
}

