/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class StudentProfile {

    private String studentUserId;
    private String studentNo;
    private String email;
    private String phone;
    private String programme;
    private String intake;

    public StudentProfile(String studentUserId, String studentNo, String email,
            String phone, String programme, String intake) {
        this.studentUserId = studentUserId;
        this.studentNo = studentNo;
        this.email = email;
        this.phone = phone;
        this.programme = programme;
        this.intake = intake;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getProgramme() {
        return programme;
    }

    public String getIntake() {
        return intake;
    }
    
            
    public String toDataString() {
        return studentUserId + "|" + studentNo + "|" + email + "|" + phone + "|"
                + programme + "|" + intake;
    }
}

