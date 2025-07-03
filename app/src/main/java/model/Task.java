package model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Lớp đại diện cho một công việc (task) cá nhân hoặc nhóm.
 */
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String title;

    public String description;
    public long date;               // timestamp đại diện cho ngày
    public String time;             // thời gian cụ thể trong ngày
    public boolean isDone;          // trạng thái hoàn thành
    public boolean isGroupTask;     // true nếu là task nhóm
    public String groupId;          // ID nhóm (nullable)
    public int priorityLevel;       // mức độ ưu tiên (1-4)

    //  Constructor đầy đủ – Room cần để khởi tạo đối tượng
    public Task(@NonNull String title, String description, String time, boolean isGroupTask, int priorityLevel, long date) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.isGroupTask = isGroupTask;
        this.priorityLevel = priorityLevel;
        this.date = date;
        this.isDone = false;
        this.groupId = null;
    }

    // Constructor rỗng – cần cho Room
    public Task() {}
}
