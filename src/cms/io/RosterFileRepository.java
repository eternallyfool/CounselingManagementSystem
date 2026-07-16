/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.Roster;
import cms.util.IdGenerator;
import java.util.ArrayList;
import java.util.List;

public class RosterFileRepository {

    private final String filePath = "data/rosters.txt";

    public List<Roster> getAllRosters() {
        List<Roster> rosters = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            Roster roster = parseRoster(line);

            if (roster != null) {
                rosters.add(roster);
            }
        }

        return rosters;
    }

    public Roster findById(String rosterId) {
        for (Roster roster : getAllRosters()) {
            if (roster.getRosterId().equals(rosterId)) {
                return roster;
            }
        }

        return null;
    }

    public List<Roster> findByCounselorId(String counselorId) {
        List<Roster> result = new ArrayList<>();

        for (Roster roster : getAllRosters()) {
            if (roster.isForCounselor(counselorId)) {
                result.add(roster);
            }
        }

        return result;
    }

    public List<Roster> findAvailableRosters() {
        List<Roster> result = new ArrayList<>();

        for (Roster roster : getAllRosters()) {
            if (roster.isAvailable()) {
                result.add(roster);
            }
        }

        return result;
    }

    public void addRoster(Roster roster) {
        FileManager.appendLine(filePath, roster.toDataString());
    }

    public boolean updateRoster(Roster updatedRoster) {
        List<Roster> rosters = getAllRosters();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (Roster roster : rosters) {
            if (roster.getRosterId().equals(updatedRoster.getRosterId())) {
                lines.add(updatedRoster.toDataString());
                updated = true;
            } else {
                lines.add(roster.toDataString());
            }
        }

        if (updated) {
            FileManager.writeLines(filePath, lines);
        }

        return updated;
    }

    public boolean deleteRoster(String rosterId) {
        List<Roster> rosters = getAllRosters();
        List<String> lines = new ArrayList<>();
        boolean deleted = false;

        for (Roster roster : rosters) {
            if (roster.getRosterId().equals(rosterId)) {
                deleted = true;
            } else {
                lines.add(roster.toDataString());
            }
        }

        if (deleted) {
            FileManager.writeLines(filePath, lines);
        }

        return deleted;
    }

    public String getNextRosterId() {
        List<String> ids = new ArrayList<>();

        for (Roster roster : getAllRosters()) {
            ids.add(roster.getRosterId());
        }

        return IdGenerator.generateNextId("R", ids);
    }

    private Roster parseRoster(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 7) {
            return null;
        }

        return new Roster(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }
}
