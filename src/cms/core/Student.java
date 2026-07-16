/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.core;

public class Student extends User {

    public Student(String userId, String username, String password, String fullName) {
        super(userId, username, password, "Student", fullName);
    }

    @Override
    public String getDashboardTitle() {
        return "Student Dashboard";
    }
}
