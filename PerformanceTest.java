package experiments;

import algorithms.AlgorithmA;
import algorithms.AlgorithmB;
import algorithms.EmergencyWorkflow;
import models.ActionType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ทดลองวัดประสิทธิภาพของ Algorithm A เทียบกับ Algorithm B
 * ตามข้อกำหนด 3.8: ทดสอบ n = 100, 1000, 10000, 50000 อย่างน้อย 5 รอบต่อขนาด
 *
 * วิธีสร้างข้อมูล n Action: วนรอบลำดับ Workflow ที่ถูกต้อง 5 ขั้นตอน
 * (CALL_RECEIVED -> ... -> CASE_CLOSED) แล้ว reset() เพื่อเริ่มรอบใหม่จนครบ n Action
 */
public class PerformanceTest {

    private static final ActionType[] CYCLE = {
            ActionType.CALL_RECEIVED,
            ActionType.TEAM_ASSIGNED,
            ActionType.VEHICLE_DISPATCHED,
            ActionType.ARRIVED_AT_SCENE,
            ActionType.CASE_CLOSED
    };

    private static final int[] SIZES = {100, 1_000, 10_000, 50_000};
    private static final int REPETITIONS = 5;

    public static void main(String[] args) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("results/experiment_results.csv"))) {
            writer.println("n,Algorithm,AverageTimeNs,Push,Pop,Comparisons");

            for (int n : SIZES) {
                runAndRecord(n, new AlgorithmA(), writer);
                runAndRecord(n, new AlgorithmB(), writer);
            }
        }
        System.out.println("บันทึกผลการทดลองที่ results/experiment_results.csv เรียบร้อย");
    }

    private static void runAndRecord(int n, EmergencyWorkflow workflow, PrintWriter writer) {
        long totalTime = 0;
        long push = 0, pop = 0, comparisons = 0;

        for (int rep = 0; rep < REPETITIONS; rep++) {
            workflow.reset();
            long repPush = 0, repPop = 0, repComparisons = 0;
            long start = System.nanoTime();

            int added = 0;
            while (added < n) {
                for (ActionType action : CYCLE) {
                    if (added >= n) break;
                    workflow.addAction(action);
                    added++;
                }
                // สะสมค่านับก่อนล้าง Stack เพื่อเริ่ม Workflow รอบใหม่
                repPush += workflow.getPushCount();
                repPop += workflow.getPopCount();
                repComparisons += workflow.getComparisonCount();
                workflow.reset();
            }

            long end = System.nanoTime();
            totalTime += (end - start);
            push += repPush;
            pop += repPop;
            comparisons += repComparisons;
        }

        long avgTime = totalTime / REPETITIONS;
        writer.println(n + "," + workflow.getAlgorithmName() + "," + avgTime + ","
                + (push / REPETITIONS) + "," + (pop / REPETITIONS) + "," + (comparisons / REPETITIONS));

        System.out.printf("n=%d | %s | avg=%d ns%n", n, workflow.getAlgorithmName(), avgTime);
    }
}
