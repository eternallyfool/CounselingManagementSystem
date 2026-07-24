/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class Appointment {
    public static final String TYPE_ONLINE = "Online";
    public static final String TYPE_WALK_IN = "WalkIn";

    public static final String STATUS_BOOKED = "Booked";
    public static final String STATUS_RESCHEDULED = "Rescheduled";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_NO_SHOW = "NoShow";

    private String appointmentId;
    private String studentUserId;
    private String counselorId;
    private String bookingType;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private String reason;
    private String status;
    private String createdBy;
    private String createdAt;

    public Appointment(String appointmentId, String studentUserId, String counselorId,
            String bookingType, String appointmentDate, String startTime, String endTime,
            String reason, String status, String createdBy, String createdAt) {
        this.appointmentId = appointmentId;
        this.studentUserId = studentUserId;
        this.counselorId = counselorId;
        this.bookingType = bookingType;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Appointment(String appointmentId, String studentUserId, String counselorId,
            String bookingType, String appointmentDate, String startTime, String endTime,
            String reason, String createdBy, String createdAt) {
        this(appointmentId, studentUserId, counselorId, bookingType, appointmentDate,
                startTime, endTime, reason, STATUS_BOOKED, createdBy, createdAt);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isForStudent(String userId) {
        return studentUserId.equals(userId);
    }

    public boolean isForCounselor(String userId) {
        return counselorId.equals(userId);
    }

    public boolean isActiveBooking() {
        return STATUS_BOOKED.equals(status) || STATUS_RESCHEDULED.equals(status);
    }

    public boolean isOnDate(String date) {
        return appointmentDate.equals(date);
    }

    public void cancel() {
        status = STATUS_CANCELLED;
    }

    public void complete() {
        status = STATUS_COMPLETED;
    }

    public void reschedule(String newDate, String newStartTime, String newEndTime) {
        appointmentDate = newDate;
        startTime = newStartTime;
        endTime = newEndTime;
        status = STATUS_RESCHEDULED;
    }

    public String getTimeRange() {
        return startTime + " - " + endTime;
    }

    public String toDataString() {
        return appointmentId + "|" + studentUserId + "|" + counselorId + "|" + bookingType + "|"
                + appointmentDate + "|" + startTime + "|" + endTime + "|" + reason + "|"
                + status + "|" + createdBy + "|" + createdAt;
    }
}
