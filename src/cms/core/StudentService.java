/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

import cms.exception.DataNotFoundException;
import cms.exception.InvalidInputException;
import cms.io.StudentProfileFileRepository;
import cms.io.UserFileRepository;
import cms.util.ValidationUtil;
import java.util.List;

public class StudentService {

    private StudentProfileFileRepository studentRepository;
    private UserFileRepository userRepository;

    public StudentService() {
        studentRepository = new StudentProfileFileRepository();
        userRepository = new UserFileRepository();
    }

    public List<StudentProfile> getAllProfiles() {
        return studentRepository.getAllProfiles();
    }

    public StudentProfile findProfileByUserId(String userId) {
        return studentRepository.findByUserId(userId);
    }

    public StudentProfile saveProfile(String studentUserId, String studentNo, String email,
            String phone, String programme, String intake) throws InvalidInputException, DataNotFoundException {
        validateProfile(studentUserId, studentNo, email, phone, programme, intake);

        User user = userRepository.findById(studentUserId);

        if (user == null || !"Student".equals(user.getRole())) {
            throw new DataNotFoundException("Student user account was not found.");
        }

        StudentProfile profile = new StudentProfile(studentUserId, studentNo.trim(), email.trim(),
                phone.trim(), programme.trim(), intake.trim());

        if (studentRepository.findByUserId(studentUserId) == null) {
            if (studentRepository.studentNoExists(studentNo)) {
                throw new InvalidInputException("Student number already exists.");
            }

            studentRepository.addProfile(profile);
        } else {
            studentRepository.updateProfile(profile);
        }

        return profile;
    }

    private void validateProfile(String studentUserId, String studentNo, String email,
            String phone, String programme, String intake) throws InvalidInputException {
        if (ValidationUtil.isEmpty(studentUserId) || ValidationUtil.isEmpty(studentNo)
                || ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(phone)
                || ValidationUtil.isEmpty(programme) || ValidationUtil.isEmpty(intake)) {
            throw new InvalidInputException("Please fill in all student profile fields.");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new InvalidInputException("Invalid email format.");
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            throw new InvalidInputException("Phone number must contain 10 to 12 digits.");
        }

        if (ValidationUtil.containsSeparator(studentNo) || ValidationUtil.containsSeparator(email)
                || ValidationUtil.containsSeparator(phone) || ValidationUtil.containsSeparator(programme)
                || ValidationUtil.containsSeparator(intake)) {
            throw new InvalidInputException("The | symbol cannot be used in saved data.");
        }
    }
}
