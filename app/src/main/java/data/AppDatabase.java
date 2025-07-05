package data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import model.Task;

@Database(entities = {Task.class}, version = 3)  //
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "task_database")
                    .fallbackToDestructiveMigration() // ✅ Tự động reset DB nếu có thay đổi
                    .build();
        }
        return instance;
    }

    public abstract TaskDao taskDao();
}
