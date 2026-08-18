import algorithms.AlgorithmA;
import algorithms.AlgorithmB;
import algorithms.EmergencyWorkflow;
import models.ActionType;
import models.WorkflowState;
import utils.InputValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Main Class: เมนูสำหรับทดสอบ Emergency Workflow
 * รองรับการเลือก Algorithm A หรือ Algorithm B, เพิ่ม Action, Undo, Redo,
 * แสดงสถานะ Event Stack / Redo Stack / currentState
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static EmergencyWorkflow workflow = new AlgorithmB();

    public static void main(String[] args) {
        System.out.println("=== ระบบ Emergency Workflow: Event Stack และ State Machine ===");
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            if (!InputValidator.isValidMenuChoice(choice, 0, 7)) {
                System.out.println(">> คำสั่งไม่ถูกต้อง กรุณาเลือกใหม่");
                continue;
            }

            switch (Integer.parseInt(choice.trim())) {
                case 1:
                    addActionFlow();
                    break;
                case 2:
                    boolean undone = workflow.undo();
                    System.out.println(undone ? ">> Undo สำเร็จ" : ">> Event Stack ว่าง ไม่สามารถ Undo ได้");
                    break;
                case 3:
                    boolean redone = workflow.redo();
                    System.out.println(redone ? ">> Redo สำเร็จ" : ">> Redo Stack ว่าง ไม่สามารถ Redo ได้");
                    break;
                case 4:
                    displayStatus();
                    break;
                case 5:
                    switchAlgorithm();
                    break;
                case 6:
                    runForcedScenario();
                    break;
                case 7:
                    workflow.reset();
                    System.out.println(">> รีเซ็ตระบบเรียบร้อย");
                    break;
                case 0:
                    running = false;
                    System.out.println("จบการทำงาน");
                    break;
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n[ใช้งาน: " + workflow.getAlgorithmName() + "]");
        System.out.println("1. เพิ่ม Action ใหม่ (ADD)");
        System.out.println("2. Undo");
        System.out.println("3. Redo");
        System.out.println("4. แสดงสถานะปัจจุบัน");
        System.out.println("5. สลับ Algorithm (A/B)");
        System.out.println("6. รันสถานการณ์บังคับ (Demo Scenario)");
        System.out.println("7. รีเซ็ตระบบ");
        System.out.println("0. ออกจากโปรแกรม");
        System.out.print("เลือกคำสั่ง: ");
    }

    private static void addActionFlow() {
        System.out.println("ตัวเลือก Action: CALL_RECEIVED, TEAM_ASSIGNED, VEHICLE_DISPATCHED, ARRIVED_AT_SCENE, CASE_CLOSED");
        System.out.print("ระบุ Action: ");
        String raw = scanner.nextLine();
        ActionType action = InputValidator.parseActionType(raw);

        if (action == null) {
            System.out.println(">> Action ไม่ถูกต้อง (Invalid Input)");
            return;
        }

        boolean success = workflow.addAction(action);
        if (success) {
            System.out.println(">> เพิ่ม " + action + " สำเร็จ | currentState = " + workflow.getCurrentState());
        } else {
            System.out.println(">> Action ผิดลำดับ! currentState ปัจจุบันคือ " + workflow.getCurrentState()
                    + " ไม่สามารถทำ " + action + " ได้");
        }
    }

    private static void displayStatus() {
        WorkflowState state = workflow.getCurrentState();
        List<ActionType> history = workflow.getEventHistory();
        List<ActionType> redoHistory = workflow.getRedoHistory();

        System.out.println("--- สถานะปัจจุบัน ---");
        System.out.println("currentState : " + state);
        System.out.println("Event Stack  (Bottom -> Top) : " + history);
        System.out.println("Redo Stack   (Top -> Bottom) : " + redoHistory);
        System.out.println("Push count   : " + workflow.getPushCount());
        System.out.println("Pop count    : " + workflow.getPopCount());
        System.out.println("Comparisons  : " + workflow.getComparisonCount());
    }

    private static void switchAlgorithm() {
        if (workflow instanceof AlgorithmA) {
            workflow = new AlgorithmB();
        } else {
            workflow = new AlgorithmA();
        }
        System.out.println(">> เปลี่ยนไปใช้ " + workflow.getAlgorithmName());
    }

    /** สถานการณ์บังคับตามข้อกำหนดของโจทย์ (ข้อ "สถานการณ์บังคับ") */
    private static void runForcedScenario() {
        workflow.reset();
        ActionType[] forced = {
                ActionType.CALL_RECEIVED,
                ActionType.TEAM_ASSIGNED,
                ActionType.VEHICLE_DISPATCHED,
                ActionType.ARRIVED_AT_SCENE,
                ActionType.CASE_CLOSED
        };

        System.out.println("--- เริ่มสถานการณ์บังคับ ---");
        for (ActionType action : forced) {
            boolean ok = workflow.addAction(action);
            System.out.println("ADD " + action + " -> " + (ok ? "OK" : "REJECTED")
                    + " | currentState = " + workflow.getCurrentState());
        }

        System.out.println("UNDO -> " + (workflow.undo() ? "OK" : "FAIL") + " | currentState = " + workflow.getCurrentState());
        System.out.println("UNDO -> " + (workflow.undo() ? "OK" : "FAIL") + " | currentState = " + workflow.getCurrentState());
        System.out.println("REDO -> " + (workflow.redo() ? "OK" : "FAIL") + " | currentState = " + workflow.getCurrentState());

        boolean newActionOk = workflow.addAction(ActionType.ARRIVED_AT_SCENE);
        System.out.println("ADD NEW ACTION (ARRIVED_AT_SCENE) -> " + (newActionOk ? "OK" : "REJECTED")
                + " | currentState = " + workflow.getCurrentState());
        System.out.println("Redo Stack ควรถูกล้างแล้ว: " + workflow.getRedoHistory());
        displayStatus();
    }
}
