package com.example.exercise;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitnessapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText edtCurrentWeight, edtTargetWeight;
    RadioGroup rgGoal;
    RadioButton rbLose, rbMaintain, rbGain;
    SeekBar seekRate;
    TextView txtRate, txtTargetDate;
    ImageView btnBack; // Đã đổi sang ImageView để khớp với XML mới
    LinearLayout btnSelectTargetDate;
    Button btnSaveProfile;

    Calendar targetCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initViews();
        loadExistingData();
        setupEvents();
    }

    private void initViews() {
        edtCurrentWeight = findViewById(R.id.edtCurrentWeight);
        edtTargetWeight = findViewById(R.id.edtTargetWeight);

        rgGoal = findViewById(R.id.rgGoal);
        rbLose = findViewById(R.id.rbLose);
        rbMaintain = findViewById(R.id.rbMaintain);
        rbGain = findViewById(R.id.rbGain);

        seekRate = findViewById(R.id.seekRate);
        txtRate = findViewById(R.id.txtRate);

        txtTargetDate = findViewById(R.id.txtTargetDate);
        btnSelectTargetDate = findViewById(R.id.btnSelectTargetDate);

        btnBack = findViewById(R.id.btnBack);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        updateDateDisplay();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (txtTargetDate != null) {
            txtTargetDate.setText(sdf.format(targetCalendar.getTime()));
        }
    }

    private void loadExistingData() {
        AppDataManager manager = AppDataManager.getInstance();
        float currentW = manager.getLatestWeight();
        float targetW = manager.getProfileTargetWeight();
        String goal = manager.getProfileGoal();

        if (edtCurrentWeight != null && currentW > 0) {
            edtCurrentWeight.setText(String.format(Locale.US, "%.1f", currentW));
        }
        if (edtTargetWeight != null && targetW > 0) {
            edtTargetWeight.setText(String.format(Locale.US, "%.1f", targetW));
        }

        if (goal != null && rgGoal != null) {
            if (goal.contains("Giảm")) {
                rgGoal.check(R.id.rbLose);
            } else if (goal.contains("Tăng")) {
                rgGoal.check(R.id.rbGain);
            } else {
                rgGoal.check(R.id.rbMaintain);
            }
        }
    }

    private void setupEvents() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View.OnClickListener dateClickListener = v -> showTargetDatePicker();
        if (btnSelectTargetDate != null) {
            btnSelectTargetDate.setOnClickListener(dateClickListener);
        } else if (txtTargetDate != null) {
            txtTargetDate.setOnClickListener(dateClickListener);
        }

        if (seekRate != null) {
            seekRate.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            double rate;
                            switch (progress) {
                                case 0:
                                    rate = 0.25;
                                    break;
                                case 1:
                                    rate = 0.5;
                                    break;
                                case 2:
                                    rate = 0.75;
                                    break;
                                default:
                                    rate = 1.0;
                                    break;
                            }
                            if (txtRate != null) {
                                txtRate.setText(rate + " kg mỗi tuần");
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {}

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {}
                    }
            );
        }

        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> {
                String currentWeightStr = edtCurrentWeight.getText().toString().trim();
                String targetWeightStr = edtTargetWeight.getText().toString().trim();

                if (currentWeightStr.isEmpty()) {
                    edtCurrentWeight.setError("Vui lòng nhập cân nặng");
                    return;
                }

                if (targetWeightStr.isEmpty()) {
                    edtTargetWeight.setError("Vui lòng nhập cân nặng mục tiêu");
                    return;
                }

                int selectedGoalId = rgGoal != null ? rgGoal.getCheckedRadioButtonId() : -1;

                if (selectedGoalId == -1) {
                    Toast.makeText(UpdateProfileActivity.this, "Vui lòng chọn mục tiêu", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    float newCurrentWeight = Float.parseFloat(currentWeightStr);
                    float newTargetWeight = Float.parseFloat(targetWeightStr);

                    String newGoal = "Giảm cân";
                    if (selectedGoalId == R.id.rbGain) {
                        newGoal = "Tăng cân";
                    } else if (selectedGoalId == R.id.rbMaintain) {
                        newGoal = "Duy trì cân nặng";
                    }

                    AppDataManager manager = AppDataManager.getInstance();
                    manager.setProfileTargetWeight(newTargetWeight);
                    manager.setProfileGoal(newGoal);

                    String todayKey = AppDataManager.getCurrentTodayKey();
                    manager.setSelectedDateKey(todayKey);
                    manager.setWeightForSelectedDate(newCurrentWeight);

                    Toast.makeText(UpdateProfileActivity.this, "Đã lưu thay đổi thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(UpdateProfileActivity.this, "Vui lòng nhập định dạng số hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showTargetDatePicker() {
        int year = targetCalendar.get(Calendar.YEAR);
        int month = targetCalendar.get(Calendar.MONTH);
        int day = targetCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                AlertDialog.THEME_HOLO_DARK,
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
                    targetCalendar.set(Calendar.YEAR, selectedYear);
                    targetCalendar.set(Calendar.MONTH, selectedMonth);
                    targetCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                year, month, day
        );

        datePickerDialog.setOnShowListener(dialog -> {
            if (datePickerDialog.getWindow() != null) {
                datePickerDialog.getWindow().setBackgroundDrawable(
                        new android.graphics.drawable.ColorDrawable(Color.parseColor("#1D1A38"))
                );
            }

            if (datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE) != null) {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.parseColor("#00BFFF"));
            }
            if (datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE) != null) {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                        .setTextColor(Color.parseColor("#8E8B9E"));
            }

            DatePicker datePicker = datePickerDialog.getDatePicker();
            if (datePicker != null) {
                datePicker.setBackgroundColor(Color.parseColor("#1D1A38"));
            }
        });

        datePickerDialog.show();
    }
}