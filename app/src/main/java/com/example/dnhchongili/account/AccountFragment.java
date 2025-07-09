package com.example.dnhchongili.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;
import com.example.dnhchongili.R;
import com.example.dnhchongili.main.MainActivity;

import java.io.IOException;

import api.ApiClient;
import data.AppDatabase;
import model.User;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    private TextView tvFullName, tvPhone, tvEmail, tvUsername;
    private ImageView ivUserPhoto;
    private Button btnLogout;

    private SharedPreferences prefs;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "🔍 onViewCreated");

        initViews(view);
        setupEncryptedPrefs();
        db = AppDatabase.getInstance(requireContext());

        loadUserInfo();
        setupLogout();
    }

    private void initViews(View view) {
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivUserPhoto = view.findViewById(R.id.ivUserPhoto);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupEncryptedPrefs() {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            prefs = EncryptedSharedPreferences.create(
                    "user_prefs",
                    masterKeyAlias,
                    requireContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            Log.d(TAG, "🔒 EncryptedSharedPreferences initialized");
        } catch (IOException e) {
            Log.e(TAG, "EncryptedSharedPreferences IO error: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "EncryptedSharedPreferences error: " + e.getMessage(), e);
        }
    }

    private void loadUserInfo() {
        new Thread(() -> {
            String username = prefs.getString("username", null);
            if (username == null) {
                Log.w(TAG, "⚠️ No username found in EncryptedSharedPreferences.");
                return;
            }

            User user = db.userDao().getByUsername(username);
            Log.d(TAG, "✅ Loaded user from local DB: " + user);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> updateUI(user));
            }
        }).start();
    }

    private void updateUI(User user) {
        if (user != null) {
            tvFullName.setText(user.getFullName());
            tvPhone.setText(user.getPhone());
            tvEmail.setText(user.getEmail());
            tvUsername.setText(user.getUsername());

            if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                Glide.with(this)
                        .load(user.getPhoto())
                        .placeholder(R.drawable.ic_user_placeholder)
                        .error(R.drawable.ic_user_placeholder)
                        .circleCrop()
                        .into(ivUserPhoto);
            } else {
                ivUserPhoto.setImageResource(R.drawable.ic_user_placeholder);
            }

        } else {
            Log.w(TAG, "⚠️ User object is null, fallback to placeholder values.");
            tvFullName.setText("N/A");
            tvPhone.setText("N/A");
            tvEmail.setText("N/A");
            tvUsername.setText("N/A");
            ivUserPhoto.setImageResource(R.drawable.ic_user_placeholder);
        }
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            Log.i(TAG, "🚪 Logging out...");
            new Thread(() -> {
                db.userDao().deleteAll();
                prefs.edit().clear().apply();
                ApiClient.disconnectSocket();
                Log.i(TAG, "✅ Local user data cleared & WebSocket disconnected");

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                }
            }).start();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "🧹 onDestroyView: cleaning up if needed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.disconnectSocket();
        Log.d(TAG, "🧹 onDestroy: WebSocket disconnected");
    }
}
