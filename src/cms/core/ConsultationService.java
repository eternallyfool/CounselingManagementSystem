/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.exception.DataNotFoundException;
import cms.exception.InvalidInputException;
import cms.io.AppointmentFileRepository;
import cms.io.ConsultationFileRepository;
import cms.util.ValidationUtil;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService {

    private ConsultationFileRepository consultationRepository;
    private AppointmentFileRepository appointmentRepository;
    private AppointmentService appointmentService;

    public ConsultationService() {
        consultationRepository = new ConsultationFileRepository();
        appointmentRepository = new AppointmentFileRepository();
        appointmentService = new AppointmentService();
    }

    public List<ConsultationRecord> getAllRecords() {
        return consultationRepository.getAllRecords();
    }

    public ConsultationRecord findByAppointmentId(String appointmentId) {
        return consultationRepository.findByAppointmentId(appointmentId);
    }

    public List<ConsultationRecord> getRecordsForCounselor(String counselorId) {
        List<ConsultationRecord> result = new ArrayList<>();

        for (ConsultationRecord record : consultationRepository.getAllRecords()) {
            Appointment appointment = appointmentRepository.findById(record.getAppointmentId());

            if (appointment != null && appointment.isForCounselor(counselorId)) {
                result.add(record);
            }
        }

        return result;
    }

    public List<ConsultationRecord> getRecordsForStudent(String studentUserId) {
        List<ConsultationRecord> result = new ArrayList<>();

        for (ConsultationRecord record : consultationRepository.getAllRecords()) {
            Appointment appointment = appointmentRepository.findById(record.getAppointmentId());

            if (appointment != null && appointment.isForStudent(studentUserId)) {
                result.add(record);
            }
        }

        return result;
    }

    public ConsultationRecord saveConsultation(String appointmentId, String notes,
            String recommendation, String followUpDate)
            throws InvalidInputException, DataNotFoundException {
        if (ValidationUtil.isEmpty(appointmentId) || ValidationUtil.isEmpty(notes)
                || ValidationUtil.isEmpty(recommendation)) {
            throw new InvalidInputException("Please fill in consultation notes and recommendation.");
        }

        if (ValidationUtil.containsSeparator(notes) || ValidationUtil.containsSeparator(recommendation)) {
            throw new InvalidInputException("The | symbol cannot be used in saved data.");
        }

        if (ValidationUtil.isEmpty(followUpDate)) {
            followUpDate = "-";
        }

        if (!"-".equals(followUpDate) && !ValidationUtil.isValidDate(followUpDate)) {
            throw new InvalidInputException("Follow-up date must use yyyy-MM-dd format, or - if not needed.");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId);

        if (appointment == null) {
            throw new DataNotFoundException("Appointment was not found.");
        }

        ConsultationRecord record = consultationRepository.findByAppointmentId(appointmentId);

        if (record == null) {
            record = new ConsultationRecord(consultationRepository.getNextRecordId(),
                    appointmentId, notes.trim(), recommendation.trim(), followUpDate.trim());
            consultationRepository.addRecord(record);
        } else {
            record.setNotes(notes.trim());
            record.setRecommendation(recommendation.trim());
            record.setFollowUpDate(followUpDate.trim());
            consultationRepository.updateRecord(record);
        }

        appointmentService.completeAppointment(appointmentId);
        return record;
    }
}

