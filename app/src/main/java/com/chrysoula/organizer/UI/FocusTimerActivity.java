package com.chrysoula.organizer.UI;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.chrysoula.organizer.R;


import java.util.Locale;

public class FocusTimerActivity extends AppCompatActivity {

    private TextView timerText;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 0;
    private long endTimeMillis = 0;

    private boolean isBreakTime = false;
    private long breakDurationMillis = 0;
    private final String[] selectedMode = {"Manual"};
    private TextView statusText;
    private Button pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.focus_timer_activity);

        timerText = findViewById(R.id.timerText);
        Button startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        Button resetButton = findViewById(R.id.resetButton);
        statusText = findViewById(R.id.statusText);
        Button modeButton = findViewById(R.id.modeButton);

        String[] modes = {"Manual", "Pomodoro 25+5", "Pomodoro 50+10"};

        modeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FocusTimerActivity.this);
            builder.setTitle("Select Mode")
                    .setItems(modes, (dialog, which) -> {
                        selectedMode[0] = modes[which];
                        modeButton.setText(modes[which]);

                        switch (modes[which]) {
                            case "Pomodoro 25+5":
                                timeLeftInMillis = 25 * 60 * 1000L;
                                breakDurationMillis = 5 * 60 * 1000L;
                                break;
                            case "Pomodoro 50+10":
                                timeLeftInMillis = 50 * 60 * 1000L;
                                breakDurationMillis = 10 * 60 * 1000L;
                                break;
                            default:
                                timeLeftInMillis = 0;
                        }

                        int minutes = (int) (timeLeftInMillis / 1000) / 60;
                        int seconds = (int) (timeLeftInMillis / 1000) % 60;
                        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                        statusText.setText("Status: " + modes[which]);
                    });

            builder.create().show();
        });

        timerText.setOnClickListener(v -> showTimePickerDialog());

        startButton.setOnClickListener(v -> {
            if (!isRunning) {
                String mode = selectedMode[0];
                if (mode.equals("Pomodoro 25+5")) {
                    isBreakTime = false;
                    breakDurationMillis = 5 * 60 * 1000;
                    endTimeMillis = System.currentTimeMillis() + (25 * 60 * 1000);
                    startTimer();
                } else if (mode.equals("Pomodoro 50+10")) {
                    isBreakTime = false;
                    breakDurationMillis = 10 * 60 * 1000;
                    endTimeMillis = System.currentTimeMillis() + (50 * 60 * 1000);
                    startTimer();
                } else {
                    if (timeLeftInMillis > 0) {
                        endTimeMillis = System.currentTimeMillis() + timeLeftInMillis;
                        startTimer();
                    }
                }
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isRunning) {
                countDownTimer.cancel();
                isRunning = false;
                timeLeftInMillis = endTimeMillis - System.currentTimeMillis();
                pauseButton.setText("Resume");
            } else if (timeLeftInMillis > 0) {
                endTimeMillis = System.currentTimeMillis() + timeLeftInMillis;
                startTimer();
                pauseButton.setText("Pause");
            }
        });

        resetButton.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            timerText.setText("00:00");
            isRunning = false;
            timeLeftInMillis = 0;
            endTimeMillis = 0;
            pauseButton.setText("Pause");
            statusText.setText("Status: Manual");
        });
    }

    private void showTimePickerDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.time_picker_dialog, null);
        NumberPicker minutesPicker = dialogView.findViewById(R.id.minutesPicker);
        NumberPicker secondsPicker = dialogView.findViewById(R.id.secondsPicker);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        new AlertDialog.Builder(this)
                .setTitle("Set Focus Time")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    int minutes = minutesPicker.getValue();
                    int seconds = secondsPicker.getValue();
                    timeLeftInMillis = (minutes * 60 + seconds) * 1000L;
                    timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startTimer() {

        isRunning = true;
        statusText.setText(isBreakTime ? "Status: Break Time" : "Status: Focus Time");

        countDownTimer = new CountDownTimer(endTimeMillis - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = endTimeMillis - System.currentTimeMillis();
                updateTimerText(timeLeftInMillis);
            }

            @Override
            public void onFinish() {
                onTimerFinish();
            }
        }.start();
    }

    private void onTimerFinish() {
        isRunning = false;

        MediaPlayer mediaPlayer = MediaPlayer.create(FocusTimerActivity.this, R.raw.timer_end_sound);
        mediaPlayer.start();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            v.vibrate(500);
        }

        if (!isBreakTime && (selectedMode[0].contains("Pomodoro"))) {
            isBreakTime = true;
            timerText.setText("Break Time!");
            statusText.setText("Status: Break Time");
            endTimeMillis = System.currentTimeMillis() + breakDurationMillis;
            startTimer();
        } else {
            timerText.setText("Time's up!");
            statusText.setText("Status: Finished");
            timeLeftInMillis = 0;
            new android.os.Handler().postDelayed(() -> {
                timerText.setText("00:00");
                statusText.setText("Status: Manual");
            }, 2000);
        }
    }

    private void updateTimerText(long millis) {
        if (millis <= 0) {
            timerText.setText("00:00");
            return;
        }
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("isRunning", isRunning);
        outState.putBoolean("isBreakTime", isBreakTime);
        outState.putString("selectedMode", selectedMode[0]);
        outState.putLong("endTimeMillis", endTimeMillis);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
        isRunning = savedInstanceState.getBoolean("isRunning");
        isBreakTime = savedInstanceState.getBoolean("isBreakTime");
        selectedMode[0] = savedInstanceState.getString("selectedMode", "Manual");
        endTimeMillis = savedInstanceState.getLong("endTimeMillis");

        if (isRunning) {
            timeLeftInMillis = endTimeMillis - System.currentTimeMillis();
            if (timeLeftInMillis > 0) {
                startTimer();
            } else {
                onTimerFinish();
            }
        } else if (timeLeftInMillis > 0) {
            updateTimerText(timeLeftInMillis);
        }

        pauseButton.setText(isRunning ? "Pause" : "Resume");
        statusText.setText(isBreakTime ? "Status: Break Time" : "Status: Focus Time");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRunning) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning) {
            timeLeftInMillis = endTimeMillis - System.currentTimeMillis();
            if (timeLeftInMillis > 0) {
                startTimer();
            } else {
                onTimerFinish();
            }
        }
    }
}

