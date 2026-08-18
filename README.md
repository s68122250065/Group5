# Group5

คำถามวิเคราะห์
1. Algorithm A ต้องค้นหา Event Stack ในกรณีใด
Algorithm A ต้องค้นหา Event Stack ตอนที่ต้องการตรวจสอบลำดับของ Action ว่าถูกต้องหรือไม่ เช่น ตอนเพิ่ม Action ใหม่ หรือเวลาที่ต้องการดูว่า Action ก่อนหน้านั้นคืออะไร
2. State Machine ช่วยให้ตรวจสอบ Action เป็น O(1) ได้อย่างไร
State Machine จะรู้ว่าใน currentstate ตอนนี้สามารถทำ Action อะไรต่อได้บ้าง จึงตรวจสอบ Action ที่เข้ามาได้โดยดูจาก State ปัจจุบันและ Transition Table โดยตรง ไม่ต้องไล่ดู Event ทั้งหมด ทำให้ใช้เวลา O(1)
3. หลัง Undo หลายครั้ง ระบบคืน currentstate อย่างไร
เมื่อกด Undo ระบบจะเอา Action ล่าสุดออกจาก Event Stack แล้วเปลี่ยน currentstate กลับไปเป็น State ก่อนหน้า ถ้ากด Undo หลายครั้งก็จะทำแบบนี้ซ้ำไปเรื่อย ๆ จนกว่าจะไม่มี Action เหลือใน Stack
4. เพราะเหตุใดต้องล้าง Redo Stack เมื่อเพิ่ม Action ใหม่
เพราะหลังจาก Undo แล้ว ถ้าเราเพิ่ม Action ใหม่ แสดงว่าเราเริ่มสร้างลำดับการทำงานใหม่ ดังนั้น Action ที่อยู่ใน Redo Stack เดิมจะไม่ตรงกับลำดับใหม่ จึงต้องล้าง Redo Stack เพื่อป้องกันการ Redo ที่ผิดลำดับ
5. Event Log ในระบบจริงควรถูกลบเมื่อ Undo หรือไม่
ไม่ควรลบ เพราะ Event Log มีไว้เก็บประวัติการทำงานของระบบ ถ้ามีการ Undo ก็ควรเก็บประวัติไว้เหมือนเดิม และอาจบันทึกเพิ่มว่าเกิดการ Undo ขึ้น เพื่อให้สามารถตรวจสอบย้อนหลังได้ว่าเคยมีการทำอะไรไปบ้าง

Correctness Invariant

Invariant ของระบบคือ หลังจากทุก Operation ระบบจะต้องรักษาความถูกต้องของสถานะและลำดับของ Action โดย currentState ต้องสอดคล้องกับ Action ล่าสุดใน eventStack และ Action ที่อยู่ใน eventStack ต้องเรียงตามลำดับ Workflow ที่กำหนดไว้ใน Transition Table

หลังจากเพิ่ม Action ใหม่

เมื่อมีการเพิ่ม Action ระบบจะตรวจสอบก่อนว่า Action สามารถทำต่อจาก currentState ได้หรือไม่

ถ้า Action ถูกต้อง ระบบจะนำ Action นั้นเข้า eventStack และเปลี่ยน currentState ไปเป็น nextState

ตัวอย่างเช่น

NEW
 ↓ CALL_RECEIVED
RECEIVED
 ↓ TEAM_ASSIGNED
ASSIGNED

ดังนั้นถ้า Action ล่าสุดคือ TEAM_ASSIGNED ค่า currentState ต้องเป็น ASSIGNED

ถ้า Action ไม่สามารถทำต่อจากสถานะปัจจุบันได้ ระบบจะไม่เพิ่ม Action นั้นเข้า Stack เพื่อป้องกันไม่ให้ Workflow ผิดลำดับ

หลังจาก Undo

เมื่อเรียก Undo() ระบบจะนำ Action ล่าสุดออกจาก eventStack แล้วนำไปเก็บไว้ใน redoStack

จากนั้น currentState ต้องย้อนกลับไปเป็นสถานะก่อนหน้า

ตัวอย่างเช่น

eventStack:
CALL_RECEIVED
TEAM_ASSIGNED
VEHICLE_DISPATCHED

สถานะปัจจุบันคือ

DISPATCHED

เมื่อ Undo() หนึ่งครั้ง

eventStack:
CALL_RECEIVED
TEAM_ASSIGNED

ดังนั้น currentState ต้องกลับเป็น

ASSIGNED

ทำให้สถานะยังคงตรงกับ Action ล่าสุดใน eventStack

หลังจาก Redo

เมื่อเรียก Redo() ระบบจะนำ Action จาก redoStack กลับมาใส่ eventStack

จากนั้น currentState ต้องเปลี่ยนกลับไปเป็นสถานะที่เกิดจาก Action นั้น

เช่น

Undo → currentState = ASSIGNED
Redo → VEHICLE_DISPATCHED

ดังนั้น

currentState = DISPATCHED

และ eventStack จะกลับมาเป็น Workflow ที่ถูกต้องเหมือนเดิม

เมื่อเพิ่ม Action ใหม่หลังจาก Undo

กรณีนี้สำคัญ เพราะเมื่อมีการเพิ่ม Action ใหม่ ระบบจะต้อง ล้าง redoStack

ตัวอย่างเช่น

CALL_RECEIVED
TEAM_ASSIGNED
VEHICLE_DISPATCHED

ถ้า Undo จะได้

eventStack:
CALL_RECEIVED
TEAM_ASSIGNED


redoStack:
VEHICLE_DISPATCHED
แต่ถ้าต่อมามีการเพิ่ม Action ใหม่ เช่น VEHICLE_DISPATCHED ในเส้นทางใหม่ ระบบต้องล้าง redoStack ก่อน เพื่อไม่ให้สามารถ Redo Action เก่าที่ไม่อยู่ใน Workflow ปัจจุบันได้

ดังนั้นในโค้ดจึงมีแนวคิดว่า

เพิ่ม Action ใหม่
→ เพิ่มเข้า eventStack
→ clear redoStack
สรุป Invariant

สามารถสรุปได้ว่า

หลังจากทุก Operation ค่า currentState ต้องสอดคล้องกับ Action ล่าสุดใน eventStack และ Action ทั้งหมดใน eventStack ต้องเรียงตาม Workflow ที่ถูกต้องจาก Transition Table หากมีการ Undo ระบบต้องสามารถย้อนกลับสถานะได้ และหากมีการเพิ่ม Action ใหม่หลังจาก Undo ต้องล้าง redoStack เพื่อป้องกันการ Redo ที่ทำให้ Workflow ผิดลำดับ
