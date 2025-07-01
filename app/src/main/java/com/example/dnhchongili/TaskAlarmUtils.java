package com.example.dnhchongili;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import model.Task;

public class TaskAlarmUtils {

    public static void setTaskAlarm(Context context, Task task, int taskId) {
        try {
            // Convert long date + time string ("HH:mm") => millis
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(task.date);

            String[] parts = task.time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            long triggerTime = calendar.getTimeInMillis();
            if (triggerTime < System.currentTimeMillis()) return;

            Intent intent = new Intent(context, TaskAlarmReceiver.class);
            intent.putExtra("taskTitle", task.title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskId, // mỗi task là 1 ID riêng
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
