/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;


public class Receptionist extends User {

    public Receptionist(String userId, String username, String password, String fullName) {
        super(userId, username, password, "Receptionist", fullName);
    }

    public Receptionist(String userId, String username, String password, String fullName, String status) {
        super(userId, username, password, "Receptionist", fullName, status);
    }

    @Override
    public String getDashboardTitle() {
        return "Receptionist Dashboard";
    }

    public boolean canGenerateQueueNumbers() {
        return true;
    }
}

