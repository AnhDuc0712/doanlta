package com.example.dnhchongili.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.dnhchongili.R;
import com.example.dnhchongili.main.MainActivity;
import com.example.dnhchongili.ui.home.HomeFragment;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import api.ApiClient;
import api.UserApi;
import data.AppDatabase;
import model.User;

public class AuthenFragment extends Fragment {

    private static final String TAG = "AuthenFragment";
    private static final int RC_SIGN_IN = 9001;

    private EditText etLoginUsername, etLoginPassword;
    private EditText etRegisterName, etRegisterUsername, etRegisterEmail, etRegisterPhone, etRegisterPassword;
    private Button btnLogin, btnRegister;
    private TextView btnToggleForm, tvErrorMessage;
    private LinearLayout layoutLogin, layoutRegister;
    private SignInButton btnGoogleSignIn;

    private SharedPreferences prefs;
    private UserApi userApi;
    private GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ApiClient.connectSocket();
        userApi = new UserApi(ApiClient.getSocket());

        initViews(view);
        initEncryptedPrefs();
        initGoogleSignIn();

        checkAutoLogin();

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
        btnGoogleSignIn.setOnClickListener(v -> startGoogleSignIn());
    }

    private void initViews(View view) {
        etLoginUsername = view.findViewById(R.id.etLoginUsername);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        etRegisterName = view.findViewById(R.id.etRegisterName);
        etRegisterUsername = view.findViewById(R.id.etRegisterUsername);
        etRegisterEmail = view.findViewById(R.id.etRegisterEmail);
        etRegisterPhone = view.findViewById(R.id.etRegisterPhone);
        etRegisterPassword = view.findViewById(R.id.etRegisterPassword);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        layoutLogin = view.findViewById(R.id.layoutLogin);
        layoutRegister = view.findViewById(R.id.layoutRegister);
        btnGoogleSignIn = view.findViewById(R.id.btnGoogleSignIn);
    }

    private void initEncryptedPrefs() {
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            prefs = EncryptedSharedPreferences.create(
                    "user_prefs",
                    masterKey,
                    requireContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(TAG, "Failed to init EncryptedSharedPreferences: " + e.getMessage(), e);
        }
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    private void checkAutoLogin() {
        if (prefs.contains("username")) {
            isProfileCompleted(completed -> {
                if (completed) {
                    syncUserData(prefs.getString("username", ""));
                    navigateToMain();
                }
            });
        }
    }

    private void toggleForm() {
        if (layoutLogin.getVisibility() == View.VISIBLE) {
            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);
            btnToggleForm.setText("Đã có tài khoản? Đăng nhập ngay");
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.GONE);
            btnToggleForm.setText("Chưa có tài khoản? Đăng ký ngay");
        }
        tvErrorMessage.setVisibility(View.GONE);
    }

    private void handleLogin() {
        String username = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        userApi.login(username, password, new UserApi.UserApiCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUi(() -> {
                    saveUserData(user, false);
                    syncUserData(username);
                    isProfileCompleted(completed -> {
                        if (completed) navigateToMain();
                        else navigateToSecondDataFragment(false);
                    });
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUi(() -> showErrorMessage(error));
            }
        });
    }

    private void handleRegister() {
        String name = etRegisterName.getText().toString().trim();
        String username = etRegisterUsername.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String phone = etRegisterPhone.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        userApi.checkUsernameExists(username, new UserApi.UserApiCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUi(() -> showErrorMessage("Tên đăng nhập đã tồn tại"));
            }

            @Override
            public void onFailure(String error) {
                User newUser = new User(name, username, email, phone, "");
                userApi.register(newUser, password, new UserApi.UserApiCallback() {
                    @Override
                    public void onSuccess(User user) {
                        runOnUi(() -> {
                            saveUserData(user, false);
                            syncUserData(username);
                            isProfileCompleted(completed -> {
                                if (completed) navigateToMain();
                                else navigateToSecondDataFragment(false);
                            });
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUi(() -> showErrorMessage(error));
                    }
                });
            }
        });
    }

    private void startGoogleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleGoogleSignIn(GoogleSignInAccount account) {
        if (account == null) return;

        String username = account.getId();
        String email = account.getEmail();
        String photo = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
        String name = account.getDisplayName() != null ? account.getDisplayName() : "";

        userApi.checkUsernameExists(username, new UserApi.UserApiCallback() {
            @Override
            public void onSuccess(User user) {
                userApi.login(username, "", new UserApi.UserApiCallback() {
                    @Override
                    public void onSuccess(User loginUser) {
                        runOnUi(() -> {
                            saveUserData(loginUser, true);
                            syncUserData(username);
                            isProfileCompleted(completed -> {
                                if (completed) navigateToMain();
                                else navigateToSecondDataFragment(true);
                            });
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUi(() -> showErrorMessage("Đăng nhập Google thất bại: " + error));
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                User newUser = new User(name, username, email, "", photo);
                userApi.register(newUser, "", new UserApi.UserApiCallback() {
                    @Override
                    public void onSuccess(User user) {
                        runOnUi(() -> {
                            saveUserData(user, true);
                            syncUserData(username);
                            navigateToSecondDataFragment(true);
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUi(() -> showErrorMessage("Đăng ký Google thất bại: " + error));
                    }
                });
            }
        });
    }

    private void saveUserData(User user, boolean isGoogle) {
        new Thread(() -> AppDatabase.getInstance(requireContext()).userDao().insert(user)).start();
        prefs.edit().putString("username", user.getUsername())
                .putBoolean("isGoogle", isGoogle).apply();
    }

    private void syncUserData(String username) {
        if (!isNetworkAvailable()) {
            Log.w(TAG, "No network. Skip sync.");
            return;
        }
        userApi.syncUserData(username, new UserApi.UserApiCallback() {
            @Override
            public void onSuccess(User user) {
                new Thread(() -> AppDatabase.getInstance(requireContext()).userDao().update(user)).start();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Sync failed: " + error);
            }
        });
    }

    private void isProfileCompleted(Callback callback) {
        new Thread(() -> {
            String username = prefs.getString("username", "");
            User user = AppDatabase.getInstance(requireContext()).userDao().getByUsername(username);
            boolean completed = user != null && !user.getFullName().isEmpty() && !user.getPhone().isEmpty();
            runOnUi(() -> callback.onResult(completed));
        }).start();
    }

    private void navigateToSecondDataFragment(boolean isGoogle) {
        SecondDataFragment fragment = new SecondDataFragment();
        Bundle args = new Bundle();
        args.putBoolean("isGoogle", isGoogle);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToMain() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showBottomNavigation();
        }
        requireActivity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, new HomeFragment())
                .commit();
    }

    private void showErrorMessage(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void runOnUi(Runnable action) {
        if (isAdded() && !requireActivity().isFinishing()) {
            requireActivity().runOnUiThread(action);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                handleGoogleSignIn(account);
            } catch (Exception e) {
                Log.e(TAG, "Google SignIn failed: " + e.getMessage());
                showErrorMessage("Đăng nhập Google thất bại");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApiClient.disconnectSocket();
    }

    private interface Callback {
        void onResult(boolean completed);
    }
}
