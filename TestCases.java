import algorithms.AlgorithmA;
import algorithms.AlgorithmB;
import algorithms.EmergencyWorkflow;
import models.ActionType;
import models.WorkflowState;

/**
 * Test Cases บังคับ (อย่างน้อย 8 กรณี ตามข้อกำหนด 3.9 / หัวข้อ Test Cases)
 * รันด้วยคำสั่ง: java -cp src test.TestCases (หลัง compile)
 * ไม่ใช้ JUnit เพื่อให้รันได้โดยไม่ต้องติดตั้ง Library เพิ่มเติม
 */
public class TestCases {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        runOnBoth("TC01_WorkflowCorrectFromStartToEnd", TestCases::tc01);
        runOnBoth("TC02_AddActionOutOfOrder", TestCases::tc02);
        runOnBoth("TC03_CloseCaseBeforeArrival", TestCases::tc03);
        runOnBoth("TC04_UndoOnce", TestCases::tc04);
        runOnBoth("TC05_UndoMultipleTimes", TestCases::tc05);
        runOnBoth("TC06_PartialRedo", TestCases::tc06);
        runOnBoth("TC07_AddNewActionAfterPartialRedoClearsRedoStack", TestCases::tc07);
        runOnBoth("TC08_UndoUntilStackEmpty", TestCases::tc08);
        runOnBoth("TC09_RedoWhenEmpty", TestCases::tc09);
        runOnBoth("TC10_AddActionAfterCaseClosed", TestCases::tc10);

        System.out.println("\n=== สรุปผล: PASS " + passed + " / FAIL " + failed + " ===");
    }

    private interface TestCase {
        void run(EmergencyWorkflow workflow, String label);
    }

    private static void runOnBoth(String name, TestCase test) {
        System.out.println("\n--- " + name + " ---");
        test.run(new AlgorithmA(), "Algorithm A");
        test.run(new AlgorithmB(), "Algorithm B");
    }

    private static void assertTrue(String label, String message, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + label + " - " + message);
        } else {
            failed++;
            System.out.println("[FAIL] " + label + " - " + message);
        }
    }

    // TC01: Workflow ถูกต้องตั้งแต่เริ่มจนจบ
    private static void tc01(EmergencyWorkflow w, String label) {
        boolean ok = w.addAction(ActionType.CALL_RECEIVED)
                && w.addAction(ActionType.TEAM_ASSIGNED)
                && w.addAction(ActionType.VEHICLE_DISPATCHED)
                && w.addAction(ActionType.ARRIVED_AT_SCENE)
                && w.addAction(ActionType.CASE_CLOSED);
        assertTrue(label, "Workflow ปกติต้องสำเร็จทุกขั้นตอนและจบที่ CLOSED",
                ok && w.getCurrentState() == WorkflowState.CLOSED);
    }

    // TC02: เพิ่ม Action ผิดลำดับ
    private static void tc02(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        boolean result = w.addAction(ActionType.ARRIVED_AT_SCENE); // ข้ามขั้นตอน
        assertTrue(label, "Action ผิดลำดับต้องถูกปฏิเสธ", !result
                && w.getCurrentState() == WorkflowState.RECEIVED);
    }

    // TC03: ปิด Case ก่อนถึงที่เกิดเหตุ
    private static void tc03(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.addAction(ActionType.TEAM_ASSIGNED);
        w.addAction(ActionType.VEHICLE_DISPATCHED);
        boolean result = w.addAction(ActionType.CASE_CLOSED); // ยังไม่ถึงที่เกิดเหตุ
        assertTrue(label, "ห้ามปิด Case ก่อนถึงที่เกิดเหตุ", !result
                && w.getCurrentState() == WorkflowState.DISPATCHED);
    }

    // TC04: Undo หนึ่งครั้ง
    private static void tc04(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.addAction(ActionType.TEAM_ASSIGNED);
        boolean undone = w.undo();
        assertTrue(label, "Undo หนึ่งครั้งต้องย้อนสถานะกลับหนึ่งขั้น", undone
                && w.getCurrentState() == WorkflowState.RECEIVED
                && w.getRedoHistory().size() == 1);
    }

    // TC05: Undo หลายครั้ง (กฎข้อ 1: หลัง Undo สองครั้ง ต้องมีสอง Action ใน Redo Stack)
    private static void tc05(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.addAction(ActionType.TEAM_ASSIGNED);
        w.addAction(ActionType.VEHICLE_DISPATCHED);
        w.undo();
        w.undo();
        assertTrue(label, "หลัง Undo สองครั้ง Redo Stack ต้องมีสอง Action และ State กลับไปที่ RECEIVED",
                w.getRedoHistory().size() == 2 && w.getCurrentState() == WorkflowState.RECEIVED);
    }

    // TC06: Redo บางส่วน (กฎข้อ 2: Redo หนึ่งครั้งต้องคืน Action ล่าสุดกลับสู่ Event Stack)
    private static void tc06(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.addAction(ActionType.TEAM_ASSIGNED);
        w.addAction(ActionType.VEHICLE_DISPATCHED);
        w.undo();
        w.undo();
        boolean redone = w.redo();
        assertTrue(label, "Redo หนึ่งครั้งต้องคืน Action ล่าสุด (TEAM_ASSIGNED) กลับมา", redone
                && w.getCurrentState() == WorkflowState.ASSIGNED
                && w.getRedoHistory().size() == 1);
    }

    // TC07: เพิ่ม Action ใหม่หลัง Redo บางส่วนต้องล้าง Redo Stack (กฎข้อ 3)
    private static void tc07(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.addAction(ActionType.TEAM_ASSIGNED);
        w.addAction(ActionType.VEHICLE_DISPATCHED);
        w.undo();
        w.undo();
        w.redo();
        w.addAction(ActionType.VEHICLE_DISPATCHED); // Action ใหม่หลัง Redo บางส่วน
        assertTrue(label, "เพิ่ม Action ใหม่หลัง Redo บางส่วนต้องล้าง Redo Stack ทั้งหมด",
                w.getRedoHistory().isEmpty());
    }

    // TC08: Undo จน Stack ว่าง
    private static void tc08(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.undo();
        boolean undoOnEmpty = w.undo(); // Undo ครั้งที่สองเมื่อ Stack ว่างแล้ว
        assertTrue(label, "Undo เมื่อ Event Stack ว่างต้องคืนค่า false และ currentState = NEW",
                !undoOnEmpty && w.getCurrentState() == WorkflowState.NEW && w.isEmpty());
    }

    // TC09: Redo เมื่อไม่มีรายการ
    private static void tc09(EmergencyWorkflow w, String label) {
        boolean redoOnEmpty = w.redo();
        assertTrue(label, "Redo เมื่อ Redo Stack ว่างต้องคืนค่า false", !redoOnEmpty);
    }

    // TC10: เพิ่ม Action หลังปิด Case
    private static void tc10(EmergencyWorkflow w, String label) {
        w.addAction(ActionType.CALL_RECEIVED);
        w.addAction(ActionType.TEAM_ASSIGNED);
        w.addAction(ActionType.VEHICLE_DISPATCHED);
        w.addAction(ActionType.ARRIVED_AT_SCENE);
        w.addAction(ActionType.CASE_CLOSED);
        boolean result = w.addAction(ActionType.CALL_RECEIVED); // ไม่มี Transition จาก CLOSED
        assertTrue(label, "ห้ามเพิ่ม Action ใด ๆ หลังจาก Case ถูกปิดแล้ว", !result
                && w.getCurrentState() == WorkflowState.CLOSED);
    }
}
