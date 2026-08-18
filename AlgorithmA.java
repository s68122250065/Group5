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
 * Algorithm A: Event Stack Method
 * -------------------------------
 * แนวคิดหลัก: ระบบไม่เก็บ currentState เป็นตัวแปรแยกต่างหาก
 * ทุกครั้งที่ต้องตรวจสอบว่า Action ใหม่ถูกลำดับหรือไม่ (หรือเมื่อถูกถาม getCurrentState())
 * อัลกอริทึมจะ "ค้นหา" Event Stack ทั้งหมด โดยไล่ตรวจสอบ Action ตั้งแต่ตัวแรก (Bottom)
 * ผ่าน Transition Table ทีละขั้น เพื่อคำนวณสถานะปัจจุบันใหม่ทุกครั้ง
 *
 * ข้อดี: โครงสร้างข้อมูลเรียบง่าย ไม่มี State ซ้ำซ้อนกับ Event Stack (Single Source of Truth)
 * ข้อจำกัด: การตรวจสอบ Action ใหม่ และการอ่านสถานะปัจจุบัน มี Time Complexity O(n)
 *          เพราะต้อง pop/push ทั้ง Stack เพื่อไล่ตรวจสอบลำดับ
 */
public class AlgorithmA implements EmergencyWorkflow {

    private Deque<EventAction> eventStack = new ArrayDeque<>();
    private Deque<EventAction> redoStack = new ArrayDeque<>();

    private long pushCount = 0;
    private long popCount = 0;
    private long comparisonCount = 0;

    /**
     * ค้นหา (scan) Event Stack ทั้งหมดเพื่อคำนวณ currentState ใหม่
     * ใช้ Temporary Stack ในการไล่จาก Bottom -> Top แล้ว Restore กลับ
     * Time Complexity: O(n) โดย n = จำนวน Action ใน Event Stack
     */
    private WorkflowState deriveCurrentState() {
        Deque<EventAction> temp = new ArrayDeque<>();

        // ย้ายทุก Action ไป Temporary Stack (ลำดับจะกลับด้าน คือ Bottom อยู่ Top ของ temp)
        while (!eventStack.isEmpty()) {
            temp.push(eventStack.pop());
            popCount++;
        }

        WorkflowState state = WorkflowState.NEW;
        while (!temp.isEmpty()) {
            EventAction action = temp.pop();
            popCount++;
            comparisonCount++;
            state = TransitionTable.getNextState(state, action.getActionType());
            eventStack.push(action); // Restore กลับสู่ Event Stack เดิม
            pushCount++;
        }
        return state;
    }

    @Override
    public boolean addAction(ActionType action) {
        WorkflowState currentState = deriveCurrentState(); // ต้องค้นหา Event Stack ทุกครั้งที่เพิ่ม Action
        comparisonCount++;
        WorkflowState nextState = TransitionTable.getNextState(currentState, action);

        if (nextState == null) {
            return false; // Action ผิดลำดับ
        }

        eventStack.push(new EventAction(action));
        pushCount++;

        if (!redoStack.isEmpty()) {
            redoStack.clear(); // กฎข้อ 3: เพิ่ม Action ใหม่ต้องล้าง Redo Stack
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
        return true;
    }

    @Override
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }
        EventAction action = redoStack.pop();
        popCount++;
        eventStack.push(action);
        pushCount++;
        return true;
    }

    @Override
    public WorkflowState getCurrentState() {
        return deriveCurrentState(); // O(n) ทุกครั้งที่อ่านสถานะ
    }

    @Override
    public List<ActionType> getEventHistory() {
        List<ActionType> history = new ArrayList<>();
        for (EventAction e : eventStack) {
            history.add(0, e.getActionType()); // ArrayDeque iterator ให้ Top -> Bottom จึงต้องกลับด้าน
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
        pushCount = 0;
        popCount = 0;
        comparisonCount = 0;
    }

    @Override
    public String getAlgorithmName() {
        return "Algorithm A (Event Stack Only)";
    }
}
