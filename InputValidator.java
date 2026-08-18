package utils;

import models.ActionType;

/**
 * ตรวจสอบความถูกต้องของ Input ที่รับมาจากผู้ใช้ (เมนู)
 */
public class InputValidator {

    /** แปลงข้อความเป็น ActionType หากไม่ตรงกับค่าใดเลยจะคืนค่า null */
    public static ActionType parseActionType(String raw) {
        if (raw == null) return null;
        String normalized = raw.trim().toUpperCase().replace(" ", "_");
        for (ActionType type : ActionType.values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isValidMenuChoice(String raw, int min, int max) {
        try {
            int value = Integer.parseInt(raw.trim());
            return value >= min && value <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
