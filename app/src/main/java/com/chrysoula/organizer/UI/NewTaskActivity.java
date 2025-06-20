package com.chrysoula.organizer.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Course;
import com.chrysoula.organizer.viewmodel.organizerViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewTaskActivity extends AppCompatActivity {

    private EditText enterText_Task_Title, enterText_Task_Description, enterDeadline;
    private Spinner courseSpinner;
    private Course selectedCourse;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task);

        // Retrieve the user ID passed from the previous screen
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        // Initialize input fields
        enterText_Task_Title = findViewById(R.id.enterText_Task_Title);
        enterText_Task_Description = findViewById(R.id.enterText_Task_Description);
        enterDeadline = findViewById(R.id.enterDeadline);
        courseSpinner = findViewById(R.id.courseSpinner);

        // Disable manual editing of deadline, open date picker instead
        enterDeadline.setFocusable(false);
        enterDeadline.setOnClickListener(v -> showDatePickerDialog());

        // Buttons
        Button buttonSaveTask = findViewById(R.id.buttonSaveTask);
        Button buttonBackTask = findViewById(R.id.buttonBackTask);



        organizerViewModel viewModel = new ViewModelProvider(this).get(organizerViewModel.class);

        // Populate course spinner with user's courses
        viewModel.getCoursesByUser(currentUserId).observe(this, courses -> {
            Log.d("NewTaskActivity", "Courses loaded: " + courses.size());
            Log.d("NewTaskActivity", "Current user ID: " + currentUserId);


            List<String> courseNames = new ArrayList<>();
            for (Course course : courses) {
                courseNames.add(course.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseSpinner.setAdapter(adapter);

            if (!courses.isEmpty()) {
                selectedCourse = courses.get(0);
            }

            courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCourse = courses.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedCourse = null;
                }
            });
        });


        // Handle save button click
        buttonSaveTask.setOnClickListener(v -> {
            String title = enterText_Task_Title.getText().toString().trim();
            String description = enterText_Task_Description.getText().toString().trim();
            String deadlineText = enterDeadline.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (deadlineText.isEmpty()) {
                Toast.makeText(this, "Please enter a deadline!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCourse == null) {
                Toast.makeText(this, "Please select a course!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse deadline string to Date
            Date deadline;
            try {
                deadline = dateFormat.parse(deadlineText);
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format! Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            // Package data and return to calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("description", description);
            resultIntent.putExtra("deadline", deadline != null ? deadline.getTime() : 0);
            resultIntent.putExtra("course_id", selectedCourse.getId());

            setResult(RESULT_OK, resultIntent);
            finish();
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        // Handle back button click
        buttonBackTask.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });
    }

    /**
     * Opens a DatePickerDialog and sets the selected date into the deadline EditText
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    Date selectedDate = calendar.getTime();
                    enterDeadline.setText(dateFormat.format(selectedDate));
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
