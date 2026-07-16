/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class QueueNumber {

    public static final String STATUS_WAITING = "Waiting";
    public static final String STATUS_CALLED = "Called";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String appointmentId;
    private String queueNo;
    private String queueStatus;
    private String issuedAt;

    public QueueNumber(String appointmentId, String queueNo, String queueStatus, String issuedAt) {
        this.appointmentId = appointmentId;
        this.queueNo = queueNo;
        this.queueStatus = queueStatus;
        this.issuedAt = issuedAt;
    }

    public QueueNumber(String appointmentId, String queueNo, String issuedAt) {
        this(appointmentId, queueNo, STATUS_WAITING, issuedAt);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getQueueNo() {
        return queueNo;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void markCalled() {
        queueStatus = STATUS_CALLED;
    }

    public void markCompleted() {
        queueStatus = STATUS_COMPLETED;
    }

    public void cancel() {
        queueStatus = STATUS_CANCELLED;
    }

    public String getDisplayQueueNo() {
        return "Q" + queueNo;
    }

    public String toDataString() {
        return appointmentId + "|" + queueNo + "|" + queueStatus + "|" + issuedAt;
    }
}

