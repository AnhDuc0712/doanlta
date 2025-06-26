package com.example.dnhchongili;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import data.AppDatabase;
import data.TaskDao;
import model.Task;

public class TaskFragment extends Fragment {

    CalendarView calendarView;
    RecyclerView recyclerView;
    Button btnAdd;
    TaskAdapter adapter;
    TaskDao dao;
    TaskViewModel viewModel;

    public TaskFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        btnAdd = view.findViewById(R.id.btnAddTask);

        AppDatabase db = AppDatabase.getInstance(getContext());
        dao = db.taskDao();

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(getContext(), null);
        recyclerView.setAdapter(adapter);

        // Luôn chuẩn hóa ngày hôm nay về 00:00 và load task
        viewModel.selectedDate = getStartOfDay(System.currentTimeMillis());
        calendarView.setDate(viewModel.selectedDate);
        loadTasksForDate(viewModel.selectedDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            viewModel.selectedDate = getStartOfDayFromDate(year, month, dayOfMonth);
            loadTasksForDate(viewModel.selectedDate);
        });

        btnAdd.setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void loadTasksForDate(long dateMillis) {
        List<Task> tasks = dao.getPersonalTasksByDateOrdered(dateMillis);
        adapter.setTasks(tasks);
    }

    private long getStartOfDay(long millis) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getStartOfDayFromDate(int year, int month, int day) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText edtTitle = dialogView.findViewById(R.id.edtTitle);
        EditText edtDescription = dialogView.findViewById(R.id.edtDescription);
        EditText edtTime = dialogView.findViewById(R.id.edtTime);
        CheckBox checkGroup = dialogView.findViewById(R.id.checkGroup);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.priority_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(spinnerAdapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm công việc mới")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String title = edtTitle.getText().toString().trim();
                    String desc = edtDescription.getText().toString().trim();
                    String time = edtTime.getText().toString().trim();
                    boolean isGroup = checkGroup.isChecked();
                    int priorityLevel = spinnerPriority.getSelectedItemPosition() + 1;

                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Task task = new Task();
                    task.title = title;
                    task.description = desc;
                    task.time = time.isEmpty() ? "--:--" : time;
                    task.isGroupTask = isGroup;
                    task.groupId = isGroup ? "nhom01" : null;
                    task.isDone = false;
                    task.date = viewModel.selectedDate;
                    task.priorityLevel = priorityLevel;

                    dao.insert(task);
                    loadTasksForDate(viewModel.selectedDate);
                    Toast.makeText(getContext(), "Đã thêm công việc!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
