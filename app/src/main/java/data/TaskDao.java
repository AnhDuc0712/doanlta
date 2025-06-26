package data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import model.Task;

import java.util.List;

/**
 * Giao diện truy cập dữ liệu cho bảng Task.
 * Dùng Room để thao tác với SQLite.
 */
@Dao
public interface TaskDao {

    // Lấy tất cả công việc theo ngày cụ thể (cho cá nhân), sắp xếp chưa làm lên trước
    @Query("SELECT * FROM tasks WHERE isGroupTask = 0 AND date = :date ORDER BY isDone ASC, priorityLevel ASC, time ASC")
    List<Task> getPersonalTasksByDate(long date);

    // Lấy công việc nhóm theo ngày và ID nhóm
    @Query("SELECT * FROM tasks WHERE isGroupTask = 1 AND groupId = :groupId AND date = :date ORDER BY isDone ASC")
    List<Task> getGroupTasksByDate(String groupId, long date);

    // Lấy toàn bộ task (ít dùng, dùng để debug hoặc thống kê)
    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    // Lấy task theo mức độ ưu tiên (dùng cho ma trận 4 ô)
    @Query("SELECT * FROM tasks WHERE priorityLevel = :level ORDER BY isDone ASC, date ASC, time ASC")
    List<Task> getTasksByPriorityLevel(int level);

    @Query("SELECT * FROM tasks WHERE isGroupTask = 0 AND date = :date ORDER BY isDone ASC, time ASC")
    List<Task> getPersonalTasksByDateOrdered(long date);

    // Thêm công việc mới
    @Insert
    void insert(Task task);

    // Cập nhật công việc (dùng khi sửa)
    @Update
    void update(Task task);

    // Xoá công việc
    @Delete
    void delete(Task task);

    // Cập nhật trạng thái hoàn thành
    @Query("UPDATE tasks SET isDone = :isDone WHERE id = :taskId")
    void updateDoneStatus(int taskId, boolean isDone);
}
