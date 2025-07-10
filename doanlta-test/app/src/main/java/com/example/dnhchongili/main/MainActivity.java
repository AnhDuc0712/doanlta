package com.example.dnhchongili.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dnhchongili.R;
import com.example.dnhchongili.account.AccountFragment;
import com.example.dnhchongili.account.AuthenFragment;
import com.example.dnhchongili.account.SecondDataFragment;
import com.example.dnhchongili.matrix.PriorityMatrixFragment;
import com.example.dnhchongili.ui.TaskFragment;
import com.example.dnhchongili.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.socket.client.Socket;
import api.SocketManager;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private SharedPreferences prefs;
    private Socket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kết nối socket 1 lần cho toàn app
        socket = SocketManager.getSocket();
        socket.connect();

        // Có thể lắng nghe sự kiện chung ở đây nếu cần (ví dụ notification...)

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_task) {
                selectedFragment = new TaskFragment();
            } else if (itemId == R.id.nav_priority) {
                selectedFragment = new PriorityMatrixFragment();
            } else if (itemId == R.id.nav_account) {
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, true);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            String token = prefs.getString("token", null);
            boolean hasAdditionalInfo = prefs.getBoolean("has_additional_info", false);
            boolean isGoogle = prefs.getBoolean("isGoogle", false);

            if (token == null || token.isEmpty()) {
                loadFragment(new AuthenFragment(), false);
                bottomNav.setVisibility(View.GONE);
            } else if (!hasAdditionalInfo) {
                SecondDataFragment fragment = new SecondDataFragment();
                Bundle args = new Bundle();
                args.putBoolean("isGoogle", isGoogle);
                fragment.setArguments(args);
                loadFragment(fragment, false);
                bottomNav.setVisibility(View.GONE);
            } else {
                loadFragment(new HomeFragment(), false);
                bottomNav.setVisibility(View.VISIBLE);
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment instanceof HomeFragment || fragment instanceof AuthenFragment) {
            getSupportFragmentManager().popBackStackImmediate(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }

        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .replace(R.id.main_frame, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void showBottomNavigation() {
        bottomNav.setVisibility(View.VISIBLE);
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.disconnect();
            socket.off(); // bỏ đăng ký sự kiện
        }
    }
}
