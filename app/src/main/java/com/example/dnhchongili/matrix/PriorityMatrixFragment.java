package com.example.dnhchongili.matrix;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dnhchongili.R;
import com.example.dnhchongili.ui.TaskAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import data.AppDatabase;
import data.TaskDao;
import model.Task;

public class PriorityMatrixFragment extends Fragment {

    RecyclerView rvUrgentImportant, rvNotUrgentImportant, rvUrgentNotImportant, rvNotUrgentNotImportant;
    TaskAdapter adapter1, adapter2, adapter3, adapter4;
    TaskDao dao;
    FloatingActionButton fabAddTaskMatrix;

    private long today;

    public PriorityMatrixFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_priority_matrix, container, false);

        today = normalizeDateToMidnight(System.currentTimeMillis());

        rvUrgentImportant = view.findViewById(R.id.recyclerUrgentImportant);
        rvNotUrgentImportant = view.findViewById(R.id.recyclerNotUrgentImportant);
        rvUrgentNotImportant = view.findViewById(R.id.recyclerUrgentNotImportant);
        rvNotUrgentNotImportant = view.findViewById(R.id.recyclerNotUrgentNotImportant);

        rvUrgentImportant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotUrgentImportant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUrgentNotImportant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotUrgentNotImportant.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter1 = new TaskAdapter(getContext(), new ArrayList<>(), 1);
        adapter2 = new TaskAdapter(getContext(), new ArrayList<>(), 2);
        adapter3 = new TaskAdapter(getContext(), new ArrayList<>(), 3);
        adapter4 = new TaskAdapter(getContext(), new ArrayList<>(), 4);


        rvUrgentImportant.setAdapter(adapter1);
        rvNotUrgentImportant.setAdapter(adapter2);
        rvUrgentNotImportant.setAdapter(adapter3);
        rvNotUrgentNotImportant.setAdapter(adapter4);

        dao = AppDatabase.getInstance(getContext()).taskDao();
        loadTasksByPriority();

        fabAddTaskMatrix = view.findViewById(R.id.fabAddTaskMatrix);
        fabAddTaskMatrix.setOnClickListener(v -> showAddDialog());

        return view;
    }

    private void loadTasksByPriority() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> allTasks = dao.getAllTasks(); // vẫn lấy toàn bộ

            List<Task> list1 = new ArrayList<>();
            List<Task> list2 = new ArrayList<>();
            List<Task> list3 = new ArrayList<>();
            List<Task> list4 = new ArrayList<>();

            for (Task task : allTasks) {
                switch (task.priorityLevel) {
                    case 1: list1.add(task); break;
                    case 2: list2.add(task); break;
                    case 3: list3.add(task); break;
                    case 4: list4.add(task); break;
                }
            }

            // ✅ Sắp xếp mỗi danh sách: chưa hoàn thành lên trước
            list1.sort((a, b) -> Boolean.compare(a.isDone, b.isDone));
            list2.sort((a, b) -> Boolean.compare(a.isDone, b.isDone));
            list3.sort((a, b) -> Boolean.compare(a.isDone, b.isDone));
            list4.sort((a, b) -> Boolean.compare(a.isDone, b.isDone));

            requireActivity().runOnUiThread(() -> {
                adapter1.setTasks(list1);
                adapter2.setTasks(list2);
                adapter3.setTasks(list3);
                adapter4.setTasks(list4);
            });
        });
    }


    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_matrix_task, null);
        EditText edtTaskName = dialogView.findViewById(R.id.edtTaskName);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);

        String[] priorities = {
                "1 - Khẩn cấp & Quan trọng",
                "2 - Không gấp mà Quan trọng",
                "3 - Khẩn cấp nhưng Không quan trọng",
                "4 - Không gấp & Không quan trọng"
        };

        spinnerPriority.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, priorities));

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm công việc")
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = edtTaskName.getText().toString().trim();
                    int priority = spinnerPriority.getSelectedItemPosition() + 1;

                    if (!name.isEmpty()) {
                        Task task = new Task();
                        task.title = name;
                        task.priorityLevel = priority;
                        task.date = today;

                        Executors.newSingleThreadExecutor().execute(() -> {
                            dao.insert(task);
                            loadTasksByPriority();
                        });
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private long normalizeDateToMidnight(long millis) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
