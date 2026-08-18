package algorithms;

import models.ActionType;
import models.WorkflowState;

import java.util.List;

/**
 * Interface กลางสำหรับ Algorithm A และ Algorithm B
 * เพื่อให้ Main.java และ PerformanceTest.java เรียกใช้งานแบบเดียวกันได้
 */
public interface EmergencyWorkflow {

    /** เพิ่ม Action ใหม่ คืนค่า true หากสำเร็จ (ถูกลำดับ), false หากผิดลำดับ */
    boolean addAction(ActionType action);

    /** Undo Action ล่าสุด คืนค่า true หากสำเร็จ, false หาก Event Stack ว่าง */
    boolean undo();

    /** Redo Action ที่เพิ่ง Undo ไป คืนค่า true หากสำเร็จ, false หาก Redo Stack ว่าง */
    boolean redo();

    /** สถานะปัจจุบันของ Case */
    WorkflowState getCurrentState();

    /** รายการ Action ทั้งหมดใน Event Stack เรียงจาก Bottom -> Top (ลำดับเวลา) */
    List<ActionType> getEventHistory();

    /** รายการ Action ที่อยู่ใน Redo Stack (Top -> Bottom) */
    List<ActionType> getRedoHistory();

    boolean isEmpty();

    /** จำนวนครั้งของ push() ที่เกิดขึ้นสะสม (สำหรับการทดลอง) */
    long getPushCount();

    /** จำนวนครั้งของ pop() ที่เกิดขึ้นสะสม (สำหรับการทดลอง) */
    long getPopCount();

    /** จำนวนครั้งของการเปรียบเทียบ/ตรวจสอบ (สำหรับการทดลอง) */
    long getComparisonCount();

    /** ล้างสถานะทั้งหมด (สำหรับเริ่มการทดลองรอบใหม่) */
    void reset();

    String getAlgorithmName();
}
