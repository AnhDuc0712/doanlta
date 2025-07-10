package com.example.dnhchongili;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
// Nếu dùng BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddGroupTaskBottomSheet extends BottomSheetDialogFragment {

    // Sửa lỗi newInstance
    public static AddGroupTaskBottomSheet newInstance() {
        return new AddGroupTaskBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_group_task, container, false);
    }

    // ... code khác (khởi tạo view, xử lý button, spinner ...)
}
