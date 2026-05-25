/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.util;

public class IdGenerator {

    public static String generateNextId(String prefix, int number) {
        return prefix + String.format("%03d", number);
    }
}
