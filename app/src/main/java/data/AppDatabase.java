package data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import model.Task;
import model.FinanceRecord;
import data.TaskDao;
import data.FinanceDao;

/**
 * Lớp quản lý Room Database của ứng dụng.
 * Khởi tạo DB duy nhất dùng Singleton Pattern.
 */
@Database(entities = {Task.class, FinanceRecord.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract TaskDao taskDao();

    public abstract FinanceDao financeDao();

    /**
     * Lấy đối tượng database duy nhất (singleton).
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "task_database"
                    )
                    .fallbackToDestructiveMigration() //  Xoá DB cũ nếu schema khác
                    .allowMainThreadQueries()         // Chỉ dùng khi test, demo
                    .build();
        }
        return instance;
    }
}
