/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.io;

import cms.core.CounselorProfile;
import java.util.ArrayList;
import java.util.List;

public class CounselorProfileFileRepository {

    private final String filePath = "data/counselor_profiles.txt";

    public List<CounselorProfile> getAllProfiles() {
        List<CounselorProfile> profiles = new ArrayList<>();

        for (String line : FileManager.readLines(filePath)) {
            CounselorProfile profile = parseProfile(line);

            if (profile != null) {
                profiles.add(profile);
            }
        }

        return profiles;
    }

    public CounselorProfile findByCounselorId(String counselorId) {
        for (CounselorProfile profile : getAllProfiles()) {
            if (profile.getCounselorId().equals(counselorId)) {
                return profile;
            }
        }

        return null;
    }

    public void addProfile(CounselorProfile profile) {
        FileManager.appendLine(filePath, profile.toDataString());
    }

    public boolean updateProfile(CounselorProfile updatedProfile) {
        List<CounselorProfile> profiles = getAllProfiles();
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        for (CounselorProfile profile : profiles) {
            if (profile.getCounselorId().equals(updatedProfile.getCounselorId())) {
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

    private CounselorProfile parseProfile(String line) {
        String[] parts = line.split("\\|", -1);

        if (parts.length != 4) {
            return null;
        }

        return new CounselorProfile(parts[0], parts[1], parts[2], parts[3]);
    }
}

