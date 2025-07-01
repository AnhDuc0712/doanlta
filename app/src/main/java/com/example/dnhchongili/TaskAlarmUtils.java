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

import android.util.Log;

public class TaskAlarmUtils {
    public static void setTaskAlarm(Context context, Task task, int taskId) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(task.date);

            String[] parts = task.time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            long triggerTime = calendar.getTimeInMillis();

            Log.d("TaskAlarmUtils", "⏰ Task: " + task.title);
            Log.d("TaskAlarmUtils", "TriggerTime: " + triggerTime);
            Log.d("TaskAlarmUtils", "CurrentTime: " + System.currentTimeMillis());

            if (triggerTime < System.currentTimeMillis()) {
                Log.w("TaskAlarmUtils", "⛔ Không đặt báo thức vì thời gian đã trôi qua.");
                return;
            }

            Intent intent = new Intent(context, TaskAlarmReceiver.class);
            intent.putExtra("taskTitle", task.title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

            Log.d("TaskAlarmUtils", "✅ Đã đặt báo thức cho task: " + task.title);
        } catch (Exception e) {
            Log.e("TaskAlarmUtils", "❌ Lỗi khi đặt báo thức", e);
        }
    }
}
