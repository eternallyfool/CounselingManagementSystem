/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;


import cms.exception.DataNotFoundException;
import cms.exception.InvalidInputException;
import cms.io.AppointmentFileRepository;
import cms.io.UserFileRepository;
import cms.util.DateUtil;
import cms.util.ValidationUtil;
import java.util.List;

public class AppointmentService {

    private AppointmentFileRepository appointmentRepository;
    private UserFileRepository userRepository;

    public AppointmentService() {
        appointmentRepository = new AppointmentFileRepository();
        userRepository = new UserFileRepository();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.getAllAppointments();
    }

    public Appointment findById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> getAppointmentsForStudent(String studentUserId) {
        return appointmentRepository.findByStudentUserId(studentUserId);
    }

    public List<Appointment> getAppointmentsForCounselor(String counselorId) {
        return appointmentRepository.findByCounselorId(counselorId);
    }

    public Appointment createAppointment(String studentUserId, String counselorId, String bookingType,
            String appointmentDate, String startTime, String endTime, String reason, String createdBy)
            throws InvalidInputException, DataNotFoundException {
        validateAppointmentFields(studentUserId, counselorId, bookingType, appointmentDate,
                startTime, endTime, reason, createdBy);
        validateStudentAndCounselor(studentUserId, counselorId);

        Appointment appointment = new Appointment(appointmentRepository.getNextAppointmentId(),
                studentUserId, counselorId, bookingType, appointmentDate, startTime, endTime,
                reason.trim(), Appointment.STATUS_BOOKED, createdBy, DateUtil.now());
        appointmentRepository.addAppointment(appointment);
        return appointment;
    }

    public void assignCounselor(String appointmentId, String counselorId)
            throws DataNotFoundException, InvalidInputException {
        Appointment appointment = requireAppointment(appointmentId);
        User counselor = userRepository.findById(counselorId);

        if (counselor == null || !"Counselor".equals(counselor.getRole())) {
            throw new DataNotFoundException("Counselor account was not found.");
        }

        if (!appointment.isActiveBooking()) {
            throw new InvalidInputException("Only booked or rescheduled appointments can be reassigned.");
        }

        appointment.setCounselorId(counselorId);
        appointmentRepository.updateAppointment(appointment);
    }

    public void rescheduleAppointment(String appointmentId, String newDate, String newStartTime, String newEndTime)
            throws DataNotFoundException, InvalidInputException {
        Appointment appointment = requireAppointment(appointmentId);

        if (!appointment.isActiveBooking()) {
            throw new InvalidInputException("Only active appointments can be rescheduled.");
        }

        if (!ValidationUtil.isValidDate(newDate)
                || !ValidationUtil.isValidTime(newStartTime)
                || !ValidationUtil.isValidTime(newEndTime)) {
            throw new InvalidInputException("Date must be yyyy-MM-dd and time must be HH:mm.");
        }

        appointment.reschedule(newDate, newStartTime, newEndTime);
        appointmentRepository.updateAppointment(appointment);
    }

    public void cancelAppointment(String appointmentId) throws DataNotFoundException, InvalidInputException {
        Appointment appointment = requireAppointment(appointmentId);

        if (Appointment.STATUS_COMPLETED.equals(appointment.getStatus())) {
            throw new InvalidInputException("Completed appointments cannot be cancelled.");
        }

        appointment.cancel();
        appointmentRepository.updateAppointment(appointment);
    }

    public void completeAppointment(String appointmentId) throws DataNotFoundException, InvalidInputException {
        Appointment appointment = requireAppointment(appointmentId);

        if (Appointment.STATUS_CANCELLED.equals(appointment.getStatus())) {
            throw new InvalidInputException("Cancelled appointments cannot be completed.");
        }

        appointment.complete();
        appointmentRepository.updateAppointment(appointment);
    }

    public List<Appointment> getAppointmentsInRange(String startDate, String endDate)
            throws InvalidInputException {
        if (!ValidationUtil.isValidDate(startDate) || !ValidationUtil.isValidDate(endDate)) {
            throw new InvalidInputException("Date must use yyyy-MM-dd format.");
        }

        return appointmentRepository.findByDateRange(startDate, endDate);
    }

    private Appointment requireAppointment(String appointmentId) throws DataNotFoundException {
        Appointment appointment = appointmentRepository.findById(appointmentId);

        if (appointment == null) {
            throw new DataNotFoundException("Appointment was not found.");
        }

        return appointment;
    }

    private void validateStudentAndCounselor(String studentUserId, String counselorId)
            throws DataNotFoundException {
        User student = userRepository.findById(studentUserId);
        User counselor = userRepository.findById(counselorId);

        if (student == null || !"Student".equals(student.getRole())) {
            throw new DataNotFoundException("Student account was not found.");
        }

        if (counselor == null || !"Counselor".equals(counselor.getRole())) {
            throw new DataNotFoundException("Counselor account was not found.");
        }
    }

    private void validateAppointmentFields(String studentUserId, String counselorId, String bookingType,
            String appointmentDate, String startTime, String endTime, String reason, String createdBy)
            throws InvalidInputException {
        if (ValidationUtil.isEmpty(studentUserId) || ValidationUtil.isEmpty(counselorId)
                || ValidationUtil.isEmpty(bookingType) || ValidationUtil.isEmpty(appointmentDate)
                || ValidationUtil.isEmpty(startTime) || ValidationUtil.isEmpty(endTime)
                || ValidationUtil.isEmpty(reason) || ValidationUtil.isEmpty(createdBy)) {
            throw new InvalidInputException("Please fill in all appointment fields.");
        }

        if (!Appointment.TYPE_ONLINE.equals(bookingType) && !Appointment.TYPE_WALK_IN.equals(bookingType)) {
            throw new InvalidInputException("Booking type must be Online or WalkIn.");
        }

        if (!ValidationUtil.isValidDate(appointmentDate)) {
            throw new InvalidInputException("Appointment date must use yyyy-MM-dd format.");
        }

        if (!ValidationUtil.isValidTime(startTime) || !ValidationUtil.isValidTime(endTime)) {
            throw new InvalidInputException("Appointment time must use HH:mm format.");
        }

        if (ValidationUtil.containsSeparator(reason)) {
            throw new InvalidInputException("The | symbol cannot be used in saved data.");
        }
    }
}

