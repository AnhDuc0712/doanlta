package data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import model.Task;
import model.User;

@Database(entities = {Task.class, User.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "task_database")
                    .fallbackToDestructiveMigration() // Tự động reset DB nếu có thay đổi
                    .build();
        }
        return instance;
    }

    public abstract TaskDao taskDao();
    public abstract UserDao userDao();
}