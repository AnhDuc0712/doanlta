package com.example.dnhchongili.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;
import com.example.dnhchongili.R;
import com.example.dnhchongili.main.MainActivity;
import com.example.dnhchongili.ui.home.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.ApiClient;
import api.UserApi;
import data.AppDatabase;
import model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecondDataFragment extends Fragment {

    private static final String TAG = "SecondDataFragment";

    private EditText etFullName, etPhone;
    private Button btnSave, btnSelectAvatar;
    private ImageView ivUserPhoto;

    private SharedPreferences prefs;
    private boolean isGoogle;
    private UserApi userApi;
    private User currentUser;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupEncryptedPrefs();
        setupRoomData();
        setupAvatarPicker();
        setupButtonListeners();
    }

    private void initViews(View view) {
        etFullName = view.findViewById(R.id.etFullName);
        etPhone = view.findViewById(R.id.etPhone);
        btnSave = view.findViewById(R.id.btnSave);
        btnSelectAvatar = view.findViewById(R.id.btnSelectAvatar);
        ivUserPhoto = view.findViewById(R.id.ivUserPhoto);
        userApi = new UserApi(ApiClient.getSocket());
        isGoogle = getArguments() != null && getArguments().getBoolean("isGoogle", false);
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
        } catch (Exception e) {
            Log.e(TAG, "Error initializing EncryptedSharedPreferences: " + e.getMessage());
        }
    }

    private void setupRoomData() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        currentUser = db.userDao().getByUsername(prefs.getString("username", ""));
        if (currentUser != null) {
            etFullName.setText(currentUser.getFullName());
            etPhone.setText(currentUser.getPhone());
            if (currentUser.getPhoto() != null && !currentUser.getPhoto().isEmpty()) {
                Glide.with(this).load(currentUser.getPhoto()).circleCrop().into(ivUserPhoto);
            }
        }
    }

    private void setupAvatarPicker() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Glide.with(this).load(uri).circleCrop().into(ivUserPhoto);
                        uploadAvatar(uri);
                    }
                });

        if (isGoogle) {
            btnSelectAvatar.setVisibility(View.GONE);
        } else {
            btnSelectAvatar.setVisibility(View.VISIBLE);
            btnSelectAvatar.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }
    }

    private void setupButtonListeners() {
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void saveUserInfo() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (!phone.matches("^\\d{9,11}$")) {
            showToast("Số điện thoại không hợp lệ");
            return;
        }

        String username = prefs.getString("username", "");
        String email = currentUser != null ? currentUser.getEmail() : "";
        String photo = currentUser != null ? currentUser.getPhoto() : "";

        User updatedUser = new User(fullName, username, email, phone, photo);

        userApi.updateUser(updatedUser, new UserApi.UserApiCallback() {
            @Override
            public void onSuccess(User user) {
                mainHandler.post(() -> {
                    saveUpdatedData(user);
                    syncUserData(username);
                    showToast("Cập nhật thông tin thành công");
                    navigateToMain();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                mainHandler.post(() -> showToast("Lỗi: " + errorMessage));
            }
        });
    }

    private void uploadAvatar(Uri uri) {
        if (!isNetworkAvailable()) {
            showToast("Không có kết nối mạng");
            return;
        }

        executor.execute(() -> {
            File file = new File(requireContext().getCacheDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                OkHttpClient client = new OkHttpClient.Builder().build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("avatar", file.getName(),
                                RequestBody.create(MediaType.parse("image/*"), file))
                        .build();

                Request request = new Request.Builder()
                        .url(ApiClient.BASE_URL + "/api/user/upload-avatar")
                        .addHeader("Authorization", "Bearer " + prefs.getString("token", ""))
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(responseBody);
                    boolean success = json.getBoolean("success");
                    if (success) {
                        String photoUrl = json.getString("photo");
                        updateLocalPhoto(photoUrl);
                        mainHandler.post(() -> {
                            Glide.with(this).load(photoUrl).circleCrop().into(ivUserPhoto);
                            showToast("Cập nhật ảnh thành công");
                        });
                    } else {
                        showToastFromBackground("Lỗi: " + json.optString("message"));
                    }
                } else {
                    showToastFromBackground("Lỗi server: " + responseBody);
                }
            } catch (IOException | JSONException e) {
                showToastFromBackground("Lỗi: " + e.getMessage());
            } finally {
                file.delete();
            }
        });
    }

    private void updateLocalPhoto(String photoUrl) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        User localUser = db.userDao().getByUsername(prefs.getString("username", ""));
        if (localUser != null) {
            localUser.setPhoto(photoUrl);
            db.userDao().update(localUser);
        }
        syncUserData(prefs.getString("username", ""));
    }

    private void saveUpdatedData(User user) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            User localUser = db.userDao().getByUsername(user.getUsername());
            if (localUser != null) {
                db.userDao().update(user);
            } else {
                db.userDao().insert(user);
            }
        });

        prefs.edit()
                .putBoolean("has_additional_info", true)
                .apply();
    }

    private void syncUserData(String username) {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No network, skipping sync");
            return;
        }

        userApi.syncUserData(username, new UserApi.UserApiCallback() {
            @Override
            public void onSuccess(User user) {
                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    User localUser = db.userDao().getByUsername(user.getUsername());
                    if (localUser != null) {
                        db.userDao().update(user);
                    } else {
                        db.userDao().insert(user);
                    }
                    Log.d(TAG, "User data synced and updated in Room");
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Sync failed: " + errorMessage);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void navigateToMain() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }

        requireActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .replace(R.id.main_frame, new HomeFragment())
                .commit();
    }

    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void showToastFromBackground(String message) {
        mainHandler.post(() -> showToast(message));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.disconnectSocket(); // Ensure WebSocket is disconnected
    }
}