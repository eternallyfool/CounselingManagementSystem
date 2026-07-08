/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.util;

import java.util.List;

public class IdGenerator {

    public static String generateNextId(String prefix, int number) {
        return prefix + number;
    }

    public static String generateNextId(String prefix, List<String> existingIds) {
        int highest = 0;

        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(id.substring(prefix.length()));

                    if (number > highest) {
                        highest = number;
                    }
                } catch (NumberFormatException e) {
                    // just ignore IDs that dont follow the prefix number pattern.
                }
            }
        }

        return generateNextId(prefix, highest + 1);
    }
}

