package com.example.dnhchongili;

import androidx.lifecycle.ViewModel;
import java.util.Calendar;

public class TaskViewModel extends ViewModel {
    public long selectedDate;

    public TaskViewModel() {
        // Mặc định là ngày hôm nay (0 giờ 0 phút)
        selectedDate = getStartOfDay(System.currentTimeMillis());
    }

    private long getStartOfDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
