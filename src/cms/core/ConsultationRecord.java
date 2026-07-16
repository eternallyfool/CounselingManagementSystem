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
    private String followUpDate;

    public ConsultationRecord(String recordId, String appointmentId, String notes, String recommendation) {
        this(recordId, appointmentId, notes, recommendation, "-");
    }

    public ConsultationRecord(String recordId, String appointmentId, String notes,
            String recommendation, String followUpDate) {
        this.recordId = recordId;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.recommendation = recommendation;
        this.followUpDate = followUpDate;
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

    public String getFollowUpDate() {
        return followUpDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public void setFollowUpDate(String followUpDate) {
        this.followUpDate = followUpDate;
    }

    public boolean hasFollowUp() {
        return followUpDate != null && !followUpDate.equals("-");
    }

    public String toDataString() {
        return recordId + "|" + appointmentId + "|" + notes + "|" + recommendation + "|" + followUpDate;
    }
}

