package com.example.dnhchongili.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dnhchongili.R;
import com.example.dnhchongili.alarm.TaskAlarmUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

import data.AppDatabase;
import model.Task;

public class AddTaskBottomSheet extends BottomSheetDialogFragment {

    public interface OnTaskAddedListener {
        void onTaskAdded();
    }

    private OnTaskAddedListener listener;
    private long selectedDate;

    public AddTaskBottomSheet(OnTaskAddedListener listener, long selectedDate) {
        this.listener = listener;
        this.selectedDate = selectedDate;
    }

    private EditText edtTitle, edtDescription, edtTime, edtGroupId;
    private Spinner spinnerPriority;
    private CheckBox checkGroup;
    private Button btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtTitle = view.findViewById(R.id.edtTitle);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtTime = view.findViewById(R.id.edtTime);
        edtGroupId = view.findViewById(R.id.edtGroupId);
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        checkGroup = view.findViewById(R.id.checkGroup);
        btnAdd = view.findViewById(R.id.btnAdd);

        edtGroupId.setVisibility(View.GONE);
        checkGroup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            edtGroupId.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.priority_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        edtTime.setOnClickListener(v -> showTimePicker());

        btnAdd.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String time = edtTime.getText().toString().trim();
            boolean isGroup = checkGroup.isChecked();
            String groupId = edtGroupId.getText().toString().trim();
            int priorityLevel = spinnerPriority.getSelectedItemPosition() + 1;

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(time)) {
                Toast.makeText(getContext(), "Vui lòng nhập tiêu đề và giờ", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task = new Task(title, description, time, isGroup, priorityLevel, selectedDate);
            task.groupId = isGroup ? (groupId.isEmpty() ? "G1" : groupId) : null;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                long taskId = db.taskDao().insert(task);  // trả về ID
                task.id = (int) taskId;  // gán lại ID cho task

                // Đặt báo thức
                TaskAlarmUtils.setTaskAlarm(requireContext(), task, task.id);

                if (listener != null) {
                    requireActivity().runOnUiThread(() -> listener.onTaskAdded());
                }
            });


            dismiss();
        });

    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Chọn thời gian")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", picker.getHour(), picker.getMinute());
            edtTime.setText(time);
        });

        picker.show(getParentFragmentManager(), "material_time_picker");
    }
}
