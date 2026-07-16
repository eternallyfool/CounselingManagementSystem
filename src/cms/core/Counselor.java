/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class Counselor extends User {

    public Counselor(String userId, String username, String password, String fullName) {
        super(userId, username, password, "Counselor", fullName);
    }

    public Counselor(String userId, String username, String password, String fullName, String status) {
        super(userId, username, password, "Counselor", fullName, status);
    }

    @Override
    public String getDashboardTitle() {
        return "Counselor Dashboard";
    }

    public boolean canAddConsultationNotes() {
        return true;
    }
}
