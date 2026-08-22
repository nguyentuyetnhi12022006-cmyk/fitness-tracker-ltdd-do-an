package com.example.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp.R;

import java.util.Locale;

public class ProfileActivity2 extends AppCompatActivity {

    private TextView txtProfileName, txtProfileEmail, txtAge, txtHeight;
    private TextView txtProfileWeight, txtTargetWeight, tvGoalDisplay, tvActivityDisplay;
    private LinearLayout layoutUpdateProfile, btnLogout;
    private TextView btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2); // Gắn đúng giao diện Hồ sơ chính

        initViews();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData(); // Tự động load lại số liệu mới nhất khi quay lại trang Hồ sơ
    }

    private void initViews() {
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtAge = findViewById(R.id.txtAge);
        txtHeight = findViewById(R.id.txtHeight);
        txtProfileWeight = findViewById(R.id.txtProfileWeight);
        txtTargetWeight = findViewById(R.id.txtTargetWeight);
        tvGoalDisplay = findViewById(R.id.tvGoalDisplay);
        tvActivityDisplay = findViewById(R.id.tvActivityDisplay); // Đã ánh xạ chính xác ID từ XML

        layoutUpdateProfile = findViewById(R.id.layoutUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
    }

    private void loadProfileData() {
        AppDataManager manager = AppDataManager.getInstance();

        if (txtProfileName != null) {
            txtProfileName.setText(manager.getProfileName());
        }
        if (txtProfileEmail != null) {
            txtProfileEmail.setText(manager.getProfileEmail());
        }
        if (txtAge != null) {
            txtAge.setText(manager.getProfileAge() + " tuổi");
        }
        if (txtHeight != null) {
            txtHeight.setText(String.format(Locale.US, "%.0f cm", manager.getProfileHeight()));
        }

        float currentWeight = manager.getLatestWeight();
        if (txtProfileWeight != null) {
            txtProfileWeight.setText(currentWeight > 0 ? String.format(Locale.US, "%.1f kg", currentWeight) : "-- kg");
        }

        if (txtTargetWeight != null) {
            txtTargetWeight.setText(String.format(Locale.US, "%.1f kg", manager.getProfileTargetWeight()));
        }

        // Xử lý làm sạch ký tự thừa cho Mục tiêu
        if (tvGoalDisplay != null) {
            String goal = manager.getProfileGoal();
            if (goal != null) {
                goal = goal.replace("›", "").trim();
            }
            tvGoalDisplay.setText(goal != null && !goal.isEmpty() ? goal : "Giảm cân");
        }

        // Xử lý làm sạch ký tự thừa cho Mức hoạt động
        if (tvActivityDisplay != null) {
            String activityLevel = manager.getCalculatedActivityLevel();
            if (activityLevel != null) {
                activityLevel = activityLevel.replace("›", "").trim();
            }
            tvActivityDisplay.setText(activityLevel != null && !activityLevel.isEmpty() ? activityLevel : "Vừa phải");
        }
    }

    private void setupEvents() {
        // Sự kiện chuyển sang màn hình Cập nhật hồ sơ
        View.OnClickListener updateClickListener = v -> {
            Intent intent = new Intent(ProfileActivity2.this, UpdateProfileActivity.class);
            startActivity(intent);
        };

        if (layoutUpdateProfile != null) {
            layoutUpdateProfile.setOnClickListener(updateClickListener);
        }
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(updateClickListener);
        }

        // Sự kiện Đăng xuất
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity2.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // ==========================================
        // ĐIỀU HƯỚNG THANH CHÂN TRANG (BOTTOM NAVIGATION)
        // ==========================================
        View menuHome = findViewById(R.id.menuHome);
        if (menuHome != null) {
            menuHome.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        View menuJournal = findViewById(R.id.menuJournal);
        if (menuJournal != null) {
            menuJournal.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity2.this, JournalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        View menuAnalytics = findViewById(R.id.menuAnalytics);
        if (menuAnalytics != null) {
            menuAnalytics.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity2.this, AnalyticsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        View menuProfile = findViewById(R.id.menuProfile);
        if (menuProfile != null) {
            menuProfile.setOnClickListener(v -> loadProfileData());
        }
    }
}