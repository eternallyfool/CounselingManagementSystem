/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;



public class CounselorProfile {

    private String counselorId;
    private String email;
    private String phone;
    private String specialization;

    public CounselorProfile(String counselorId, String email, String phone, String specialization) {
        this.counselorId = counselorId;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String[] getSpecializationList() {
        return specialization.split("\\s*;\\s*");
    }

    public String toDataString() {
        return counselorId + "|" + email + "|" + phone + "|" + specialization;
    }
}

