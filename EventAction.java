package models;

import java.time.LocalDateTime;

/**
 * Data Model แทน Action หนึ่งรายการที่ถูกบันทึกใน Event Stack
 * ตามข้อกำหนด 3.1 ต้องมีข้อมูล Action ครบถ้วน
 */
public class EventAction {

    private final ActionType actionType;
    private final LocalDateTime timestamp;

    public EventAction(ActionType actionType) {
        this.actionType = actionType;
        this.timestamp = LocalDateTime.now();
    }

    public ActionType getActionType() {
        return actionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return actionType.name();
    }
}
