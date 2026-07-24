/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.Appointment;
import cms.util.DateUtil;
import cms.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class AppointmentFileRepository {

    private final String filePath = "data/appointments.txt";

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            Appointment appointment = parseAppointment(line);

            if (appointment != null) {
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    public Appointment findById(String appointmentId) {
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return appointment;
            }
        }

        return null;
    }

    public List<Appointment> findByStudentUserId(String studentUserId) {
        List<Appointment> result = new ArrayList<>();

        for (Appointment appointment : getAllAppointments()) {
            if (appointment.isForStudent(studentUserId)) {
                result.add(appointment);
            }
        }

        return result;
    }

    public List<Appointment> findByCounselorId(String counselorId) {
        List<Appointment> result = new ArrayList<>();

        for (Appointment appointment : getAllAppointments()) {
            if (appointment.isForCounselor(counselorId)) {
                result.add(appointment);
            }
        }

        return result;
    }

    public List<Appointment> findByDateRange(String startDate, String endDate) {
        List<Appointment> result = new ArrayList<>();

        for (Appointment appointment : getAllAppointments()) {
            if (DateUtil.isDateInRange(appointment.getAppointmentDate(), startDate, endDate)) {
                result.add(appointment);
            }
        }

        return result;
    }

    public void addAppointment(Appointment appointment) {
        FileManager.appendLine(filePath, appointment.toDataString());
    }

    public boolean updateAppointment(Appointment updatedAppointment) {
        List<Appointment> appointments = getAllAppointments();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId().equals(updatedAppointment.getAppointmentId())) {
                lines.add(updatedAppointment.toDataString());
                updated = true;
            } else {
                lines.add(appointment.toDataString());
            }
        }

        if (updated) {
            FileManager.writeLines(filePath, lines);
        }

        return updated;
    }

    public String getNextAppointmentId() {
        List<String> ids = new ArrayList<>();

        for (Appointment appointment : getAllAppointments()) {
            ids.add(appointment.getAppointmentId());
        }

        return IdGenerator.generateNextId("A", ids);
    }

    private Appointment parseAppointment(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 11) {
            return null;
        }

        return new Appointment(parts[0], parts[1], parts[2], parts[3], parts[4],
                parts[5], parts[6], parts[7], parts[8], parts[9], parts[10]);
    }
}
