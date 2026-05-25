/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class ConsultationRecord {
    private String recordId;
    private String appointmentId;
    private String notes;
    private String recommendation;

    public ConsultationRecord(String recordId, String appointmentId, String notes, String recommendation) {
        this.recordId = recordId;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.recommendation = recommendation;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getNotes() {
        return notes;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
