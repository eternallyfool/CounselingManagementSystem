/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.exception.DataNotFoundException;
import cms.io.AppointmentFileRepository;
import cms.io.QueueNumberFileRepository;
import cms.util.DateUtil;
import java.util.ArrayList;
import java.util.List;

public class QueueService {

    private QueueNumberFileRepository queueRepository;
    private AppointmentFileRepository appointmentRepository;

    public QueueService() {
        queueRepository = new QueueNumberFileRepository();
        appointmentRepository = new AppointmentFileRepository();
    }

    public List<QueueNumber> getAllQueueNumbers() {
        return queueRepository.getAllQueueNumbers();
    }

    public QueueNumber findByAppointmentId(String appointmentId) {
        return queueRepository.findByAppointmentId(appointmentId);
    }

    public QueueNumber generateQueueNumber(String appointmentId) throws DataNotFoundException {
        Appointment appointment = appointmentRepository.findById(appointmentId);

        if (appointment == null) {
            throw new DataNotFoundException("Appointment was not found.");
        }

        QueueNumber existing = queueRepository.findByAppointmentId(appointmentId);

        if (existing != null) {
            return existing;
        }

        QueueNumber queueNumber = new QueueNumber(appointmentId, queueRepository.getNextQueueNo(),
                QueueNumber.STATUS_WAITING, DateUtil.now());
        queueRepository.addQueueNumber(queueNumber);
        return queueNumber;
    }

    public List<QueueNumber> getQueueNumbersForStudent(String studentUserId) {
        List<QueueNumber> result = new ArrayList<>();
        List<Appointment> appointments = appointmentRepository.findByStudentUserId(studentUserId);

        for (Appointment appointment : appointments) {
            QueueNumber queueNumber = queueRepository.findByAppointmentId(appointment.getAppointmentId());

            if (queueNumber != null) {
                result.add(queueNumber);
            }
        }

        return result;
    }
}

