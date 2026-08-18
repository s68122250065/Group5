package algorithms;

import models.ActionType;
import models.EventAction;
import models.WorkflowState;
import utils.TransitionTable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Algorithm B: Event Stack ร่วมกับ State Machine
 * -----------------------------------------------
 * แนวคิดหลัก: เก็บ currentState เป็นตัวแปรเดียว (Cache ของสถานะล่าสุด)
 * ทุกครั้งที่เพิ่ม Action ใหม่ ใช้ Transition Table ตรวจสอบและอัปเดต currentState ทันที
 * โดยไม่ต้องไล่สแกน Event Stack ทั้งหมด
 *
 * ข้อดี: ตรวจสอบ Action ใหม่และอ่านสถานะปัจจุบันได้ใน O(1)
 * ข้อจำกัด: ต้องดูแลให้ currentState สอดคล้องกับ Event Stack เสมอ (Invariant)
 *          หากมีจุดใดอัปเดต currentState ผิดพลาด ระบบจะเสีย Consistency ทันที
 */
public class AlgorithmB implements EmergencyWorkflow {

    private Deque<EventAction> eventStack = new ArrayDeque<>();
    private Deque<EventAction> redoStack = new ArrayDeque<>();
    private WorkflowState currentState = WorkflowState.NEW;

    private long pushCount = 0;
    private long popCount = 0;
    private long comparisonCount = 0;

    @Override
    public boolean addAction(ActionType action) {
        comparisonCount++;
        WorkflowState nextState = TransitionTable.getNextState(currentState, action); // O(1)

        if (nextState == null) {
            return false; // Action ผิดลำดับ
        }

        eventStack.push(new EventAction(action));
        pushCount++;
        currentState = nextState;

        if (!redoStack.isEmpty()) {
            redoStack.clear(); // กฎข้อ 3: เพิ่ม Action ใหม่หลัง Redo บางส่วนต้องล้าง Redo Stack
        }
        return true;
    }

    @Override
    public boolean undo() {
        if (eventStack.isEmpty()) {
            return false;
        }
        EventAction last = eventStack.pop();
        popCount++;
        redoStack.push(last);
        pushCount++;
        // ย้อนสถานะกลับไปยังสถานะก่อนหน้า Action ที่ถูก Undo
        currentState = TransitionTable.getPreviousState(currentState, last.getActionType());
        comparisonCount++;
        return true;
    }

    @Override
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        EventAction action = redoStack.pop();
        popCount++;
        comparisonCount++;
        WorkflowState nextState = TransitionTable.getNextState(currentState, action.getActionType());
        eventStack.push(action);
        pushCount++;
        currentState = nextState;
        return true;
    }

    @Override
    public WorkflowState getCurrentState() {
        return currentState; // O(1)
    }

    @Override
    public List<ActionType> getEventHistory() {
        List<ActionType> history = new ArrayList<>();
        for (EventAction e : eventStack) {
            history.add(0, e.getActionType());
        }
        return history;
    }

    @Override
    public List<ActionType> getRedoHistory() {
        List<ActionType> history = new ArrayList<>();
        for (EventAction e : redoStack) {
            history.add(e.getActionType());
        }
        return history;
    }

    @Override
    public boolean isEmpty() {
        return eventStack.isEmpty();
    }

    @Override
    public long getPushCount() {
        return pushCount;
    }

    @Override
    public long getPopCount() {
        return popCount;
    }

    @Override
    public long getComparisonCount() {
        return comparisonCount;
    }

    @Override
    public void reset() {
        eventStack.clear();
        redoStack.clear();
        currentState = WorkflowState.NEW;
        pushCount = 0;
        popCount = 0;
        comparisonCount = 0;
    }

    @Override
    public String getAlgorithmName() {
        return "Algorithm B (Event Stack + State Machine)";
    }
}
