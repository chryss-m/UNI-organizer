package com.chrysoula.organizer.UI;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.chrysoula.organizer.R;

import com.chrysoula.organizer.model.Course;
import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.viewmodel.organizerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Inspirational quotes array
    private final String[] quotes = {
            "“Small steps every day lead to big changes.”",
            "“Stay positive, work hard, make it happen.”",
            "“Your only limit is your mind.”",
            "“Push yourself, because no one else is going to do it for you.”"
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //LOAD UI LAYOUT

        //GET USER ID PASSES VIA INTENT
        int currentUserId = getIntent().getIntExtra("USER_ID", -1);
        Log.d("MainActivity", "Received USER_ID: " + currentUserId);


        //UI ELEMENT INITIALIZATION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ProgressBar progressBar = findViewById(R.id.taskProgressBar);
        TextView progressText = findViewById(R.id.taskProgressText);
        TextView quoteTextView = findViewById(R.id.inspirationalQuote);
        Button timerButton = findViewById(R.id.focusTimerButton);
        TextView averageTextView = findViewById(R.id.averageValue);
        TextView todayLessonText = findViewById(R.id.todayLesson);
        TextView todayDeadlineText = findViewById(R.id.todayDeadline);
        CardView averageCard = findViewById(R.id.averageCard);
        CardView todayCard = findViewById(R.id.todayCard);
        ImageView settingsImage=findViewById(R.id.settings);

        

        //VIEW MODEL SET UP
        organizerViewModel viewModel = new ViewModelProvider(this).get(organizerViewModel.class);
        viewModel.setCurrentUserId(currentUserId);


        //ANIMATIONS
        Animation fadeIn1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeIn2 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        averageCard.startAnimation(fadeIn1);
        todayCard.startAnimation(fadeIn2);

        //AVERAGE GRADE OBSERVE
        viewModel.getAverageGradeByUser(currentUserId).observe(this, avg -> {
            averageTextView.setText(String.format(Locale.getDefault(), "%.2f", avg));
        });




        //OBSERVE TASKS TO UPDATE PROGRESS BAR
        viewModel.getTasksByUser(currentUserId).observe(this, tasks -> {
            if (tasks != null && !tasks.isEmpty()) {
                int completed = 0;
                for (Task task : tasks) {
                    if (task.isStatus()) completed++;
                }
                int total = tasks.size();
                int progress = (int) ((completed * 100.0f) / total);
                progressBar.setProgress(progress);
                progressText.setText(progress + "% Completed");
            } else {
                progressBar.setProgress(0);
                progressText.setText("No Tasks");
            }
        });

        //DISPLAY TODAY'S LESSONS
        String todayDayName = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date());
        viewModel.getCoursesForDayByUser(currentUserId, todayDayName).observe(this, courses -> {
            StringBuilder lessonsBuilder = new StringBuilder();
            if (courses != null) {
                for (Course course : courses) {
                    if (!course.isCompleted()) {
                        lessonsBuilder.append("• ")
                                .append(course.getName())
                                .append(" at ")
                                .append(course.getTime())
                                .append("\n");
                    }
                }
            }
            todayLessonText.setText(
                    lessonsBuilder.length() == 0 ? "Lessons: None" : "Lessons:\n" + lessonsBuilder.toString().trim()
            );
        });

        //DISPLAY TODAY'S DEADLINES
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        viewModel.getTasksByUser(currentUserId).observe(this, tasks -> {
            StringBuilder deadlineBuilder = new StringBuilder();
            if (tasks != null) {
                for (Task task : tasks) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String taskDeadlineDate = dateFormat.format(task.getDeadline());


                    if (!task.isStatus() && todayDate.equals(taskDeadlineDate)) {
                        deadlineBuilder.append("• ").append(task.getTitle()).append("\n");
                    }
                }
            }
            todayDeadlineText.setText(
                    deadlineBuilder.length() == 0 ? "Deadlines: None" : "Deadlines:\n" + deadlineBuilder.toString().trim()
            );
        });


        //PICK RANDOM QUOTE
        Random random = new Random();
        int index = random.nextInt(quotes.length);
        quoteTextView.setText(quotes[index]);

        //FOCUS TIMER BUTTON
        timerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FocusTimerActivity.class);
            startActivity(intent);
        });

        //SETTINGS
        settingsImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        //USEFUL LINKS
        Button btnEclass = findViewById(R.id.btnEclass);
        Button btnWebmail = findViewById(R.id.btnWebmail);
        Button btnGithub = findViewById(R.id.btnGithub);

        btnEclass.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://eclass.uoa.gr/main/portfolio.php"));
            startActivity(browserIntent);
        });

        btnWebmail.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://webmail.noc.uoa.gr/src/login.php"));
            startActivity(browserIntent);
        });

        btnGithub.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"));
            startActivity(browserIntent);
        });






        //BOTTOM NAVIGATION
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_courses) {
                Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            } else if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_tasks) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });



    }



}
