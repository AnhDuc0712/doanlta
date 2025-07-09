package com.example.dnhchongili.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnhchongili.R;
import com.example.dnhchongili.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class TaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskViewModel taskViewModel;
    private FloatingActionButton fabAdd;
    private CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        fabAdd = view.findViewById(R.id.fabAddTask);
        calendarView = view.findViewById(R.id.calendarView);

        adapter = new TaskAdapter(requireContext(), List.of());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Ngày mặc định: hôm nay
        long todayStart = taskViewModel.getStartOfDay(System.currentTimeMillis());
        taskViewModel.selectedDate = todayStart;

        // Load task ngày hôm nay
        taskViewModel.getTasksForDate(requireContext(), todayStart)
                .observe(getViewLifecycleOwner(), tasks -> adapter.setTasks(tasks));

        // Khi chọn ngày mới
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            selected.set(Calendar.MILLISECOND, 0);

            long selectedDateMillis = selected.getTimeInMillis();
            taskViewModel.selectedDate = selectedDateMillis;

            taskViewModel.getTasksForDate(requireContext(), selectedDateMillis)
                    .observe(getViewLifecycleOwner(), updatedTasks -> adapter.setTasks(updatedTasks));
        });

        // Khi bấm nút thêm
        fabAdd.setOnClickListener(v -> {
            long selectedDate = taskViewModel.selectedDate;

            AddTaskBottomSheet bottomSheet = new AddTaskBottomSheet(() -> {
                taskViewModel.getTasksForDate(requireContext(), selectedDate)
                        .observe(getViewLifecycleOwner(), updatedTasks -> adapter.setTasks(updatedTasks));
            }, selectedDate);

            bottomSheet.show(getParentFragmentManager(), "AddTaskBottomSheet");
        });
    }
}
