package models;

/**
 * ประเภทของ Action ที่เกิดขึ้นในระบบ Emergency Workflow
 * ลำดับที่ถูกต้องของ Workflow คือ
 * CALL_RECEIVED -> TEAM_ASSIGNED -> VEHICLE_DISPATCHED -> ARRIVED_AT_SCENE -> CASE_CLOSED
 */
public enum ActionType {
    CALL_RECEIVED,
    TEAM_ASSIGNED,
    VEHICLE_DISPATCHED,
    ARRIVED_AT_SCENE,
    CASE_CLOSED
}
