/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.exception.DataNotFoundException;
import cms.exception.InvalidInputException;
import cms.io.RosterFileRepository;
import cms.io.UserFileRepository;
import cms.util.ValidationUtil;
import java.util.List;

public class RosterService {

    private RosterFileRepository rosterRepository;
    private UserFileRepository userRepository;

    public RosterService() {
        rosterRepository = new RosterFileRepository();
        userRepository = new UserFileRepository();
    }

    public List<Roster> getAllRosters() {
        return rosterRepository.getAllRosters();
    }

    public List<Roster> getRostersForCounselor(String counselorId) {
        return rosterRepository.findByCounselorId(counselorId);
    }

    public List<Roster> getAvailableRosters() {
        return rosterRepository.findAvailableRosters();
    }

    public Roster addRoster(String counselorId, String workDate, String startTime,
            String endTime, String room, String availabilityStatus)
            throws InvalidInputException, DataNotFoundException {
        validateRoster(counselorId, workDate, startTime, endTime, room, availabilityStatus);

        User counselor = userRepository.findById(counselorId);

        if (counselor == null || !"Counselor".equals(counselor.getRole())) {
            throw new DataNotFoundException("Counselor account was not found.");
        }

        Roster roster = new Roster(rosterRepository.getNextRosterId(), counselorId,
                workDate, startTime, endTime, room.trim(), availabilityStatus);
        rosterRepository.addRoster(roster);
        return roster;
    }

    public void updateRosterStatus(String rosterId, String availabilityStatus)
            throws DataNotFoundException, InvalidInputException {
        if (!isValidAvailabilityStatus(availabilityStatus)) {
            throw new InvalidInputException("Invalid roster status.");
        }

        Roster roster = rosterRepository.findById(rosterId);

        if (roster == null) {
            throw new DataNotFoundException("Roster was not found.");
        }

        roster.setAvailabilityStatus(availabilityStatus);
        rosterRepository.updateRoster(roster);
    }
    
        public void updateRoster(String rosterId, String counselorId, String workDate, String startTime,
            String endTime, String room, String availabilityStatus)
            throws InvalidInputException, DataNotFoundException {
        validateRoster(counselorId, workDate, startTime, endTime, room, availabilityStatus);
        
        if (rosterRepository.findById(rosterId) == null) {
            throw new DataNotFoundException("Roster was not found.");
        }
        
        Roster updatedRoster = new Roster(rosterId, counselorId, workDate, startTime, endTime, room.trim(), availabilityStatus);
        rosterRepository.updateRoster(updatedRoster);
    }

    public void deleteRoster(String rosterId) throws DataNotFoundException {
        if (!rosterRepository.deleteRoster(rosterId)) {
            throw new DataNotFoundException("Roster was not found.");
        }
    }
    

    private void validateRoster(String counselorId, String workDate, String startTime,
            String endTime, String room, String availabilityStatus) throws InvalidInputException {
        if (ValidationUtil.isEmpty(counselorId) || ValidationUtil.isEmpty(workDate)
                || ValidationUtil.isEmpty(startTime) || ValidationUtil.isEmpty(endTime)
                || ValidationUtil.isEmpty(room) || ValidationUtil.isEmpty(availabilityStatus)) {
            throw new InvalidInputException("Please fill in all roster fields.");
        }

        if (!ValidationUtil.isValidDate(workDate)) {
            throw new InvalidInputException("Date must use yyyy-MM-dd format.");
        }

        if (!ValidationUtil.isValidTime(startTime) || !ValidationUtil.isValidTime(endTime)) {
            throw new InvalidInputException("Time must use HH:mm format.");
        }

        if (ValidationUtil.containsSeparator(room)) {
            throw new InvalidInputException("The | symbol cannot be used in saved data.");
        }

        if (!isValidAvailabilityStatus(availabilityStatus)) {
            throw new InvalidInputException("Invalid roster status.");
        }
    }

    private boolean isValidAvailabilityStatus(String status) {
        return Roster.STATUS_AVAILABLE.equals(status) || Roster.STATUS_UNAVAILABLE.equals(status)
                || Roster.STATUS_FULL.equals(status) || Roster.STATUS_LEAVE.equals(status);
    }
}

