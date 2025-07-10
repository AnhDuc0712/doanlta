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

import org.json.JSONObject;

import java.io.IOException;

import api.SocketManager;
import data.AppDatabase;
import data.UserDao;            // import đúng
import model.User;
import io.socket.client.Socket;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";

    private TextView tvFullName, tvPhone, tvEmail, tvUsername;
    private ImageView ivUserPhoto;
    private Button btnLogout;

    private SharedPreferences prefs;
    private AppDatabase db;
    private Socket socket;

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
        socket = SocketManager.getSocket();
        if (!socket.connected()) socket.connect();

        loadUserInfoLocal();

        String username = prefs.getString("username", null);

        // Gửi socket để sync dữ liệu user mới nhất từ server
        if (username != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("username", username);
                socket.emit("sync_user", data);
                Log.d(TAG, "🔗 Emitted sync_user for: " + username);
            } catch (Exception e) {
                Log.e(TAG, "Error emitting sync_user", e);
            }
        }

        // Lắng nghe kết quả sync từ server
        socket.on("sync_user_response", args -> {
            if (args.length > 0) {
                JSONObject resp = (JSONObject) args[0];
                boolean success = resp.optBoolean("success", false);
                if (success) {
                    JSONObject userJson = resp.optJSONObject("user");
                    if (userJson != null) {
                        User user = new User();
                        user.setUsername(userJson.optString("username", ""));
                        user.setFullName(userJson.optString("name", ""));
                        user.setEmail(userJson.optString("email", ""));
                        user.setPhone(userJson.optString("phone", ""));
                        user.setPhoto(userJson.optString("photo", ""));
                        insertOrUpdateUser(user);

                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> updateUI(user));
                        }
                        Log.d(TAG, "✅ User info updated from server");
                    }
                } else {
                    Log.w(TAG, "❌ Sync user from server failed: " + resp.optString("message", ""));
                }
            }
        });

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

    // Hàm tự xử lý insert hoặc update User theo username
    private void insertOrUpdateUser(User user) {
        new Thread(() -> {
            UserDao userDao = db.userDao();
            User existing = userDao.getByUsername(user.getUsername());
            if (existing == null) {
                userDao.insert(user);
            } else {
                user.id = existing.id; // Giữ id cũ để update đúng row
                userDao.update(user);
            }
        }).start();
    }

    // Đọc user từ DB local để load UI nhanh
    private void loadUserInfoLocal() {
        new Thread(() -> {
            String username = prefs.getString("username", null);
            if (username == null) {
                Log.w(TAG, "⚠️ No username found in EncryptedSharedPreferences.");
                return;
            }

            UserDao userDao = db.userDao();
            User user = userDao.getByUsername(username);
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
                if (socket != null) socket.disconnect();
                Log.i(TAG, "✅ Local user data cleared & Socket disconnected");

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
        if (socket != null) socket.off("sync_user_response");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) socket.off("sync_user_response");
        Log.d(TAG, "🧹 onDestroy: Socket listener removed");
    }
}
