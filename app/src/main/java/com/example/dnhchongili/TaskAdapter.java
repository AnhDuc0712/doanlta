package com.example.dnhchongili;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import data.AppDatabase;
import model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.txtTitle.setText(task.title);
        holder.txtDesc.setText(task.description != null ? task.description : "");
        holder.txtDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy", task.date));
        holder.txtTime.setText(task.time != null ? task.time : "--:--");
        holder.txtType.setText(task.isGroupTask && task.groupId != null ? "Nhóm: " + task.groupId : "Cá nhân");

        if (task.isDone) {
            holder.txtStatus.setText("Đã hoàn thành");
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50")); // xanh lá
            holder.btnDone.setVisibility(View.GONE); // ẩn nút nếu đã hoàn thành
        } else {
            holder.txtStatus.setText("Chưa hoàn thành");
            holder.txtStatus.setTextColor(Color.parseColor("#FF5722")); // cam
            holder.btnDone.setVisibility(View.VISIBLE);
        }

        holder.btnDone.setOnClickListener(v -> {
            task.isDone = true;
            AppDatabase.getInstance(context).taskDao().update(task);

            // Lấy lại danh sách đã sắp xếp
            List<Task> updatedTasks = AppDatabase.getInstance(context)
                    .taskDao()
                    .getPersonalTasksByDate(task.date);

            setTasks(updatedTasks); // cập nhật lại danh sách hiển thị

            Toast.makeText(context, "Công việc đã được đánh dấu hoàn thành!", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, txtDate, txtTime, txtType, txtStatus;
        Button btnDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtType = itemView.findViewById(R.id.txtType);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }
}
