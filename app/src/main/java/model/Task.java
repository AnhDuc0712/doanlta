package model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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

    // Ngày thực hiện công việc, lưu dạng timestamp (milliseconds)
    public long date;

    // Thời gian cụ thể trong ngày (ví dụ: "08:30", "15:00")
    public String time;

    // Trạng thái hoàn thành: true nếu đã làm xong
    public boolean isDone;

    // Là công việc nhóm hay cá nhân
    public boolean isGroupTask;

    // Nếu là task nhóm → lưu ID nhóm (nếu không, có thể để null)
    public String groupId;

    // Mức độ ưu tiên (1-4) tương ứng với ma trận Eisenhower
    /*
        1 = Khẩn cấp và Quan trọng
        2 = Không gấp nhưng Quan trọng
        3 = Khẩn cấp nhưng không Quan trọng
        4 = Không khẩn cấp và không Quan trọng
    */
    public int priorityLevel;
}
