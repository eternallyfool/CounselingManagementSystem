/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class Admin extends User {

    public Admin(String userId, String username, String password, String fullName) {
        super(userId, username, password, "Admin", fullName);
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard";
    }
}
