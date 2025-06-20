package com.chrysoula.organizer.UI;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Course;
import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.viewmodel.organizerViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class EditTaskActivity extends AppCompatActivity{

    private EditText editTitle, editDescription, editDeadline;
    private CheckBox checkBoxCompleted;
    private Spinner courseSpinner;

    private Course selectedCourse;
    private organizerViewModel viewModel;

    private int taskId;
    private int courseId;
    private int currentUserId;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);

        //INITIALIZE VIEW MODEL
        viewModel = new ViewModelProvider(this).get(organizerViewModel.class);

        currentUserId = getIntent().getIntExtra("user_id", -1);

        //INITIALIZE UI ELEMENTS
        courseSpinner = findViewById(R.id.courseSpinner);
        editTitle = findViewById(R.id.edittext_Task_Title);
        editDescription = findViewById(R.id.editText_Task_Description);
        editDeadline = findViewById(R.id.editDeadline);
        checkBoxCompleted = findViewById(R.id.checkBox_Task_Completed);
        Button updateButton = findViewById(R.id.buttonUpdateTask);
        Button cancelButton = findViewById(R.id.buttonCancelTask);

        //RETRIEVE TASK AND USER DATA FROM INTENT
        taskId = getIntent().getIntExtra("task_id", -1);
        courseId = getIntent().getIntExtra("course_id", -1);
        currentUserId = getIntent().getIntExtra("user_id", -1);
        String title = getIntent().getStringExtra("task_title");
        String description = getIntent().getStringExtra("task_description");
        long deadlineMillis = getIntent().getLongExtra("task_deadline", 0);
        boolean status = getIntent().getBooleanExtra("task_status", false);

        Date deadline = new Date(deadlineMillis);

        //POPULATE THE UI FIELDS WITH EXISTING TASK DATA
        editTitle.setText(title);
        editDescription.setText(description);
        editDeadline.setText(sdf.format(deadline));
        checkBoxCompleted.setChecked(status);

        //SET UP THE DATE PICKER DIALOG FOR DEADLINE INPUT
        editDeadline.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(editDeadline.getText().toString()));
            } catch (ParseException e) {
                calendar.setTime(new Date());
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, y, m, d) -> {
                calendar.set(y, m, d);
                editDeadline.setText(sdf.format(calendar.getTime()));
            }, year, month, day).show();
        });

        //LOAD COURSE LIST INTO THE SPINNER
        viewModel.getCoursesByUser(currentUserId).observe(this, courses -> {
            ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            courseSpinner.setAdapter(adapter);

            for (int i = 0; i < courses.size(); i++) {
                if (courses.get(i).getId() == courseId) {
                    courseSpinner.setSelection(i);
                    selectedCourse = courses.get(i);
                    break;
                }
            }

            courseSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                    selectedCourse = (Course) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    selectedCourse = null;
                }
            });
        });

        //UPDATE TASK WHEN THE UPDATE BUTTON IS CLICKED
        updateButton.setOnClickListener(v -> {
            Log.d("EditTaskActivity", "Updating task with ID: " + taskId + ", UserID: " + currentUserId);

            String newTitle = editTitle.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();
            boolean isCompleted = checkBoxCompleted.isChecked();

            Date newDeadline;
            try {
                newDeadline = sdf.parse(editDeadline.getText().toString());
            } catch (ParseException e) {
                newDeadline = new Date();
            }

            Task updatedTask = new Task(currentUserId,newTitle, newDescription, newDeadline,
                    selectedCourse != null ? selectedCourse.getId() : -1, isCompleted);
            updatedTask.setId(taskId);


            viewModel.updateTask(updatedTask);

            finish();
        });

        cancelButton.setOnClickListener(v -> finish());
    }
}
