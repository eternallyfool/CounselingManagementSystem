/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.StudentProfile;
import java.util.ArrayList;
import java.util.List;

public class StudentProfileFileRepository {

    private final String filePath = "data/students.txt";

    public List<StudentProfile> getAllProfiles() {
        List<StudentProfile> profiles = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            StudentProfile profile = parseProfile(line);

            if (profile != null) {
                profiles.add(profile);
            }
        }

        return profiles;
    }

    public StudentProfile findByUserId(String userId) {
        for (StudentProfile profile : getAllProfiles()) {
            if (profile.getStudentUserId().equals(userId)) {
                return profile;
            }
        }

        return null;
    }

    public void addProfile(StudentProfile profile) {
        FileManager.appendLine(filePath, profile.toDataString());
    }

    public boolean updateProfile(StudentProfile updatedProfile) {
        List<StudentProfile> profiles = getAllProfiles();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (StudentProfile profile : profiles) {
            if (profile.getStudentUserId().equals(updatedProfile.getStudentUserId())) {
                lines.add(updatedProfile.toDataString());
                updated = true;
            } else {
                lines.add(profile.toDataString());
            }
        }

        if (updated) {
            FileManager.writeLines(filePath, lines);
        }

        return updated;
    }

    public boolean studentNoExists(String studentNo) {
        for (StudentProfile profile : getAllProfiles()) {
            if (profile.getStudentNo().equalsIgnoreCase(studentNo)) {
                return true;
            }
        }

        return false;
    }

    private StudentProfile parseProfile(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 6) {
            return null;
        }

        return new StudentProfile(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
    }
}

