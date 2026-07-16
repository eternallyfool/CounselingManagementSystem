/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class Roster {

    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_UNAVAILABLE = "Unavailable";
    public static final String STATUS_FULL = "Full";
    public static final String STATUS_LEAVE = "Leave";

    private String rosterId;
    private String counselorId;
    private String workDate;
    private String startTime;
    private String endTime;
    private String room;
    private String availabilityStatus;

    public Roster(String rosterId, String counselorId, String workDate, String startTime,
            String endTime, String room, String availabilityStatus) {
        this.rosterId = rosterId;
        this.counselorId = counselorId;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.availabilityStatus = availabilityStatus;
    }

    public String getRosterId() {
        return rosterId;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(availabilityStatus);
    }

    public boolean isForCounselor(String userId) {
        return counselorId.equals(userId);
    }

    public String getTimeRange() {
        return startTime + " - " + endTime;
    }

    public String toDataString() {
        return rosterId + "|" + counselorId + "|" + workDate + "|" + startTime + "|"
                + endTime + "|" + room + "|" + availabilityStatus;
    }
}

