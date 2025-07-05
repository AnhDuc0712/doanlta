package com.example.dnhchongili.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dnhchongili.R;
import com.example.dnhchongili.main.MainActivity;
import api.ApiClient;
import api.UserApi;
import model.User;
import model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private LinearLayout layoutLogin, layoutRegister;
    private Button btnLogin, btnRegister;
    private TextView btnToggleForm;
    private boolean isLoginMode = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        layoutLogin = view.findViewById(R.id.layoutLogin);
        layoutRegister = view.findViewById(R.id.layoutRegister);
        btnToggleForm = view.findViewById(R.id.btnToggleForm);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnToggleForm.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            layoutLogin.setVisibility(isLoginMode ? View.VISIBLE : View.GONE);
            layoutRegister.setVisibility(isLoginMode ? View.GONE : View.VISIBLE);
            btnToggleForm.setText(isLoginMode ? "Chưa có tài khoản? Đăng ký ngay" : "Đã có tài khoản? Đăng nhập");
        });

        btnLogin.setOnClickListener(v -> {
            String email = ((EditText) view.findViewById(R.id.etLoginEmail)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.etLoginPassword)).getText().toString();

            UserApi api = ApiClient.getClient().create(UserApi.class);
            api.login(new User(email, password)).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        saveLogin(response.body());
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    } else {
                        Toast.makeText(getContext(), "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRegister.setOnClickListener(v -> {
            String name = ((EditText) view.findViewById(R.id.etRegisterName)).getText().toString();
            String email = ((EditText) view.findViewById(R.id.etRegisterEmail)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.etRegisterPassword)).getText().toString();

            UserApi api = ApiClient.getClient().create(UserApi.class);
            api.register(new User(name, email, password)).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        toggleForms(); // chuyển về login
                    } else {
                        Toast.makeText(getContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void toggleForms() {
        layoutLogin.setVisibility(isLoginMode ? View.VISIBLE : View.GONE);
        layoutRegister.setVisibility(isLoginMode ? View.GONE : View.VISIBLE);
        btnToggleForm.setText(isLoginMode ? "Chưa có tài khoản? Đăng ký ngay" : "Đã có tài khoản? Đăng nhập");
    }

    private void saveLogin(UserResponse response) {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        prefs.edit()
                .putString("token", response.token)
                .putString("email", response.user.Email)
                .apply();
    }
}