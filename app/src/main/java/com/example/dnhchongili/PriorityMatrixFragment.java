package com.example.dnhchongili;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import data.AppDatabase;
import data.TaskDao;
import model.Task;

public class PriorityMatrixFragment extends Fragment {

    RecyclerView rvUrgentImportant, rvNotUrgentImportant, rvUrgentNotImportant, rvNotUrgentNotImportant;
    TaskAdapter adapter1, adapter2, adapter3, adapter4;
    TaskDao dao;

    public PriorityMatrixFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_priority_matrix, container, false);

        rvUrgentImportant = view.findViewById(R.id.recyclerUrgentImportant);
        rvNotUrgentImportant = view.findViewById(R.id.recyclerNotUrgentImportant);
        rvUrgentNotImportant = view.findViewById(R.id.recyclerUrgentNotImportant);
        rvNotUrgentNotImportant = view.findViewById(R.id.recyclerNotUrgentNotImportant);

        rvUrgentImportant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotUrgentImportant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUrgentNotImportant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotUrgentNotImportant.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter1 = new TaskAdapter(getContext(), new ArrayList<>());
        adapter2 = new TaskAdapter(getContext(), new ArrayList<>());
        adapter3 = new TaskAdapter(getContext(), new ArrayList<>());
        adapter4 = new TaskAdapter(getContext(), new ArrayList<>());

        rvUrgentImportant.setAdapter(adapter1);
        rvNotUrgentImportant.setAdapter(adapter2);
        rvUrgentNotImportant.setAdapter(adapter3);
        rvNotUrgentNotImportant.setAdapter(adapter4);

        dao = AppDatabase.getInstance(getContext()).taskDao();
        loadTasksByPriority();

        return view;
    }

    private void loadTasksByPriority() {
        List<Task> allTasks = dao.getAllTasks();

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

        adapter1.setTasks(list1);
        adapter2.setTasks(list2);
        adapter3.setTasks(list3);
        adapter4.setTasks(list4);
    }
}
