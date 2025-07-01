package data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import model.Task;

@Dao
public interface TaskDao {

    // ✅ Thêm task
    @Insert
    long insert(Task task);

    // ✅ Cập nhật task
    @Update
    void update(Task task);

    // ✅ Xoá task
    @Delete
    void delete(Task task);

    // ✅ Cập nhật trạng thái hoàn thành
    @Query("UPDATE tasks SET isDone = :isDone WHERE id = :taskId")
    void updateDoneStatus(int taskId, boolean isDone);

    // ✅ Lấy theo ngày - LiveData
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY isDone ASC, isGroupTask DESC, priorityLevel ASC, time ASC")
    LiveData<List<Task>> getTasksByDate(long date);

    // ✅ Lấy theo ngày - List thường
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY isDone ASC, isGroupTask DESC, priorityLevel ASC, time ASC")
    List<Task> getAllTasksByDate(long date);

    // ✅ Lấy task cá nhân
    @Query("SELECT * FROM tasks WHERE isGroupTask = 0 AND date = :date ORDER BY isDone ASC, priorityLevel ASC, time ASC")
    List<Task> getPersonalTasksByDate(long date);

    // ✅ Lấy task nhóm theo groupId
    @Query("SELECT * FROM tasks WHERE isGroupTask = 1 AND groupId = :groupId AND date = :date ORDER BY isDone ASC")
    List<Task> getGroupTasksByDate(String groupId, long date);

    // ✅ Lấy tất cả task
    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    // ✅ Lấy theo mức độ ưu tiên
    @Query("SELECT * FROM tasks WHERE priorityLevel = :level ORDER BY isDone ASC, date ASC, time ASC")
    List<Task> getTasksByPriorityLevel(int level);
}
