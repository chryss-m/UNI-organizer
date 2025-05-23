package com.chrysoula.organizer.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.chrysoula.organizer.R;

public class NewCourseActivity extends AppCompatActivity {

    private EditText editTextName, editTextProfessor, editTextRoom, editTextGrade, editTextECTS;
    private EditText editTextTime,editTextDay;
    private CheckBox checkBoxCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSharedElementEnterTransition(new ChangeBounds());
        getWindow().setSharedElementReturnTransition(new ChangeBounds());


        setContentView(R.layout.new_course);

        editTextName = findViewById(R.id.enterCourse_name);
        editTextProfessor = findViewById(R.id.enter_professor);
        editTextDay = findViewById(R.id.enterDay);
        editTextTime = findViewById(R.id.enterTime);
        editTextRoom = findViewById(R.id.entercourse_room);
        editTextGrade = findViewById(R.id.entercourse_grade);
        editTextECTS = findViewById(R.id.entercourse_ects);
        checkBoxCompleted = findViewById(R.id.checkbox_completed);

        Button buttonSave = findViewById(R.id.buttonSaveCourse);
        Button buttonBack = findViewById(R.id.buttonBackCourse);

        buttonSave.setOnClickListener(v -> saveCourse());
        buttonBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        editTextDay.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select a Day");

            builder.setSingleChoiceItems(daysOfWeek, -1, (dialog, which) -> {
                editTextDay.setText(daysOfWeek[which]);
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        editTextTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        int roundedMinute = (minute < 30) ? 0 : 30;
                        @SuppressLint("DefaultLocale") String selectedTime = String.format("%02d:%02d", hourOfDay, roundedMinute);
                        editTextTime.setText(selectedTime);
                    }, 12, 0, true);

            timePickerDialog.show();
        });


    }

    private void saveCourse() {
        String name = editTextName.getText().toString().trim();
        String professor = editTextProfessor.getText().toString().trim();
        String date = editTextDay.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String room = editTextRoom.getText().toString().trim();
        String gradeStr = editTextGrade.getText().toString().trim();
        String ectsStr = editTextECTS.getText().toString().trim();



        if (name.isEmpty() || professor.isEmpty() || date.isEmpty() || time.isEmpty() || ectsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int grade = 0;
        if (!gradeStr.isEmpty()) {
            try {
                grade = Integer.parseInt(gradeStr);
                if (grade < 0 || grade > 10) {
                    Toast.makeText(this, "Grade must be between 0 and 10", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Grade must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int ects;
        try {
            ects = Integer.parseInt(ectsStr);
            if (ects < 0) {
                Toast.makeText(this, "ECTS must be a positive number", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ECTS must be a number", Toast.LENGTH_SHORT).show();
            return;
        }



        if (!time.matches("\\d{2}:\\d{2}")) {
            Toast.makeText(this, "Invalid time format. Use HH:MM", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent resultIntent = new Intent();
        resultIntent.putExtra("course_name", name);
        resultIntent.putExtra("course_professor", professor);
        resultIntent.putExtra("course_date", date);
        resultIntent.putExtra("course_time", time);
        resultIntent.putExtra("course_room", room);
        resultIntent.putExtra("course_grade",grade);
        resultIntent.putExtra("course_ECTS", ects);

        boolean isCompleted = checkBoxCompleted.isChecked();
        resultIntent.putExtra("course_completed", isCompleted);

        setResult(RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

    }
}