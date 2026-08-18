package utils;

import models.ActionType;
import models.WorkflowState;

import java.util.HashMap;
import java.util.Map;

/**
 * Transition Table ตามที่กำหนดในโจทย์กลุ่มที่ 5
 *
 * Current State   Action                Next State
 * NEW             CALL_RECEIVED         RECEIVED
 * RECEIVED        TEAM_ASSIGNED         ASSIGNED
 * ASSIGNED        VEHICLE_DISPATCHED    DISPATCHED
 * DISPATCHED      ARRIVED_AT_SCENE      ON_SCENE
 * ON_SCENE        CASE_CLOSED           CLOSED
 *
 * ใช้ Nested Map เพื่อค้นหา Next State ได้ใน O(1)
 */
public class TransitionTable {

    private static final Map<WorkflowState, Map<ActionType, WorkflowState>> TABLE = new HashMap<>();

    static {
        addTransition(WorkflowState.NEW, ActionType.CALL_RECEIVED, WorkflowState.RECEIVED);
        addTransition(WorkflowState.RECEIVED, ActionType.TEAM_ASSIGNED, WorkflowState.ASSIGNED);
        addTransition(WorkflowState.ASSIGNED, ActionType.VEHICLE_DISPATCHED, WorkflowState.DISPATCHED);
        addTransition(WorkflowState.DISPATCHED, ActionType.ARRIVED_AT_SCENE, WorkflowState.ON_SCENE);
        addTransition(WorkflowState.ON_SCENE, ActionType.CASE_CLOSED, WorkflowState.CLOSED);
    }

    private static void addTransition(WorkflowState from, ActionType action, WorkflowState to) {
        TABLE.computeIfAbsent(from, k -> new HashMap<>()).put(action, to);
    }

    /** คืนค่า Next State หากถูกต้องตาม Transition Table มิฉะนั้นคืนค่า null (Action ผิดลำดับ) */
    public static WorkflowState getNextState(WorkflowState currentState, ActionType action) {
        Map<ActionType, WorkflowState> row = TABLE.get(currentState);
        if (row == null) return null;
        return row.get(action);
    }

    /**
     * หาสถานะก่อนหน้า (ใช้ตอน Undo) โดยไล่หา State ที่มี Transition
     * ด้วย Action ที่กำหนด แล้วไปสู่ currentState
     * ตารางนี้เล็กและคงที่ (5 แถว) จึงถือว่าเป็น O(1)
     */
    public static WorkflowState getPreviousState(WorkflowState currentState, ActionType lastAction) {
        for (Map.Entry<WorkflowState, Map<ActionType, WorkflowState>> entry : TABLE.entrySet()) {
            WorkflowState from = entry.getKey();
            WorkflowState to = entry.getValue().get(lastAction);
            if (to == currentState) {
                return from;
            }
        }
        return WorkflowState.NEW;
    }
}
