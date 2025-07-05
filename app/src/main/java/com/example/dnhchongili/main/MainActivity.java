package com.example.dnhchongili.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.dnhchongili.account.AccountFragment;
import com.example.dnhchongili.ui.home.HomeFragment;
import com.example.dnhchongili.matrix.PriorityMatrixFragment;
import com.example.dnhchongili.R;
import com.example.dnhchongili.ui.TaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.contains("token"); // Kiểm tra token thay vì user_id

        if (!isLoggedIn) {
            // Hiển thị AccountFragment để đăng nhập/đăng ký
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AccountFragment())
                    .commit();
        } else {
            // Hiển thị giao diện chính như bình thường
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        bottomNav = findViewById(R.id.bottom_nav);

        // Mở trang mặc định là Trang chính
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_task) {
                loadFragment(new TaskFragment());
            } else if (id == R.id.nav_priority) {
                loadFragment(new PriorityMatrixFragment());
            } else if (id == R.id.nav_account) {
                loadFragment(new AccountFragment());
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }
}