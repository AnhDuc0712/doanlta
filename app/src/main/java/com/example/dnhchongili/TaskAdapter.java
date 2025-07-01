package com.example.dnhchongili;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import data.AppDatabase;
import model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private int priorityLevelFixed; // ✅ để giữ task trong đúng ma trận

    public TaskAdapter(Context context, List<Task> taskList, int priorityLevelFixed) {
        this.context = context;
        this.taskList = taskList;
        this.priorityLevelFixed = priorityLevelFixed;
    }
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

        String[] levels = {
                "Khẩn cấp và Quan trọng",
                "Không gấp nhưng Quan trọng",
                "Khẩn cấp nhưng không Quan trọng",
                "Không khẩn cấp và không Quan trọng"
        };
        if (task.priorityLevel >= 1 && task.priorityLevel <= 4) {
            holder.txtPriority.setText("Ưu tiên: " + levels[task.priorityLevel - 1]);
        } else {
            holder.txtPriority.setText("Ưu tiên: Không xác định");
        }

        if (task.isGroupTask && task.groupId != null) {
            holder.txtType.setText("Nhóm: " + task.groupId);
            holder.txtType.setTextColor(Color.parseColor("#3F51B5"));
            holder.itemView.setBackgroundResource(R.drawable.bg_task_group);
        } else {
            holder.txtType.setText("Cá nhân");
            holder.txtType.setTextColor(Color.parseColor("#009688"));
            holder.itemView.setBackgroundResource(R.drawable.bg_task_personal);
        }

        if (task.isDone) {
            holder.txtStatus.setText("Đã hoàn thành");
            holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.btnDone.setVisibility(View.GONE);
        } else {
            holder.txtStatus.setText("Chưa hoàn thành");
            holder.txtStatus.setTextColor(Color.parseColor("#FF5722"));
            holder.btnDone.setVisibility(View.VISIBLE);
        }

        holder.btnDone.setOnClickListener(v -> {
            task.isDone = true;
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(context).taskDao().update(task);
                ((android.app.Activity) context).runOnUiThread(() -> {
                    taskList.set(holder.getAdapterPosition(), task);
                    notifyItemChanged(holder.getAdapterPosition());
                    Toast.makeText(context, "Đã đánh dấu hoàn thành!", Toast.LENGTH_SHORT).show();
                });
            });
        });

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.inflate(R.menu.task_item_menu);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    showEditDialog(task);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, txtDate, txtTime, txtType, txtStatus, txtPriority;
        TextView btnDone;
        ImageButton btnMenu;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtType = itemView.findViewById(R.id.txtType);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtPriority = itemView.findViewById(R.id.txtPriority);
            btnDone = itemView.findViewById(R.id.btnDone);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }

    private void showEditDialog(Task task) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null);
        EditText edtTitle = dialogView.findViewById(R.id.edtTitle);
        EditText edtDesc = dialogView.findViewById(R.id.edtDescription);
        EditText edtTime = dialogView.findViewById(R.id.edtTime);
        Spinner spnPriority = dialogView.findViewById(R.id.spinnerPriority);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        if (btnAdd != null) btnAdd.setVisibility(View.GONE);

        edtTitle.setText(task.title);
        edtDesc.setText(task.description);
        edtTime.setText(task.time);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.priority_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPriority.setAdapter(adapter);
        spnPriority.setSelection(task.priorityLevel - 1);

        edtTime.setOnClickListener(v -> {
            int hour = 8, minute = 0;
            if (task.time != null && task.time.contains(":")) {
                String[] parts = task.time.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            }

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(hour)
                    .setMinute(minute)
                    .setTitleText("Chọn giờ")
                    .build();

            picker.addOnPositiveButtonClickListener(dialog -> {
                String selectedTime = String.format("%02d:%02d", picker.getHour(), picker.getMinute());
                edtTime.setText(selectedTime);
            });

            picker.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "time_picker");
        });

        new android.app.AlertDialog.Builder(context)
                .setTitle("Chỉnh sửa công việc")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    task.title = edtTitle.getText().toString().trim();
                    task.description = edtDesc.getText().toString().trim();
                    task.time = edtTime.getText().toString();
                    task.priorityLevel = spnPriority.getSelectedItemPosition() + 1;

                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase.getInstance(context).taskDao().update(task);
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            notifyDataSetChanged();
                            Toast.makeText(context, "Đã cập nhật task!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
