package models;

/**
 * สถานะ (State) ของ Case ในระบบ Emergency Workflow
 * ใช้โดย Algorithm B (Event Stack + State Machine) เป็นหลัก
 * และใช้เป็นค่าที่คำนวณได้จาก Event Stack ใน Algorithm A
 */
public enum WorkflowState {
    NEW,
    RECEIVED,
    ASSIGNED,
    DISPATCHED,
    ON_SCENE,
    CLOSED
}
