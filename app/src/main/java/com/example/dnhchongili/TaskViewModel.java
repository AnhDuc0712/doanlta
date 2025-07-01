package com.example.dnhchongili;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import data.AppDatabase;
import model.Task;
import android.content.Context;

public class TaskViewModel extends ViewModel {
    public long selectedDate;

    public TaskViewModel() {
        selectedDate = getStartOfDay(System.currentTimeMillis());
    }

    public static long getStartOfDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public LiveData<List<Task>> getTasksForDate(Context context, long date) {
        AppDatabase db = AppDatabase.getInstance(context);
        return db.taskDao().getTasksByDate(date);
    }
}
