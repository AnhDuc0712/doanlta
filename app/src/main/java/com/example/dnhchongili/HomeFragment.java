package com.example.dnhchongili;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import model.Task;

public class HomeFragment extends Fragment {

    private RecyclerView rvTodayTasks;
    private com.example.dnhchongili.TaskAdapter taskAdapter;
    private TextView tvSummary;
    private com.example.dnhchongili.TaskViewModel taskViewModel;

    public HomeFragment() {
        // Bắt buộc phải có constructor rỗng
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Gắn layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ view
        rvTodayTasks = view.findViewById(R.id.rvTodayTasks);
        tvSummary = view.findViewById(R.id.tvSummary);

        // Thiết lập RecyclerView
        taskAdapter = new TaskAdapter(requireContext(), new ArrayList<>());
        rvTodayTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTodayTasks.setAdapter(taskAdapter);

        // Lấy ViewModel
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        // Tính thời gian bắt đầu của hôm nay
        long todayStart = TaskViewModel.getStartOfDay(System.currentTimeMillis());

        // Lấy dữ liệu từ DB
        taskViewModel.getTasksForDate(requireContext(), todayStart)
                .observe(getViewLifecycleOwner(), this::updateTaskList);

        return view;
    }

    private void updateTaskList(List<Task> tasks) {
        taskAdapter.setTasks(tasks);

        int total = tasks.size();
        int completed = 0;
        for (Task task : tasks) {
            if (task.isDone) completed++;
        }

        tvSummary.setText("Hôm nay: " + completed + " / " + total + " công việc đã hoàn thành");
    }
}
