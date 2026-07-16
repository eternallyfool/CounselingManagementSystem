/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.exception.DataNotFoundException;
import cms.exception.InvalidInputException;
import cms.io.CounselorProfileFileRepository;
import cms.io.UserFileRepository;
import cms.util.ValidationUtil;
import java.util.List;

public class CounselorProfileService {

    private CounselorProfileFileRepository profileRepository;
    private UserFileRepository userRepository;

    public CounselorProfileService() {
        profileRepository = new CounselorProfileFileRepository();
        userRepository = new UserFileRepository();
    }

    public List<CounselorProfile> getAllProfiles() {
        return profileRepository.getAllProfiles();
    }

    public CounselorProfile findByCounselorId(String counselorId) {
        return profileRepository.findByCounselorId(counselorId);
    }

    public CounselorProfile saveProfile(String counselorId, String email, String phone, String specialization)
            throws InvalidInputException, DataNotFoundException {
        if (ValidationUtil.isEmpty(counselorId) || ValidationUtil.isEmpty(email)
                || ValidationUtil.isEmpty(phone) || ValidationUtil.isEmpty(specialization)) {
            throw new InvalidInputException("Please fill in all counselor profile fields.");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new InvalidInputException("Invalid email format.");
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            throw new InvalidInputException("Phone number must contain 10 to 12 digits.");
        }

        if (ValidationUtil.containsSeparator(email) || ValidationUtil.containsSeparator(phone)
                || ValidationUtil.containsSeparator(specialization)) {
            throw new InvalidInputException("The | symbol cannot be used in saved data.");
        }

        User counselor = userRepository.findById(counselorId);

        if (counselor == null || !"Counselor".equals(counselor.getRole())) {
            throw new DataNotFoundException("Counselor account was not found.");
        }

        CounselorProfile profile = new CounselorProfile(counselorId, email.trim(),
                phone.trim(), specialization.trim());

        if (profileRepository.findByCounselorId(counselorId) == null) {
            profileRepository.addProfile(profile);
        } else {
            profileRepository.updateProfile(profile);
        }

        return profile;
    }
}

