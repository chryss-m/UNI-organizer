package com.chrysoula.organizer.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Course;
import com.chrysoula.organizer.viewmodel.organizerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CourseActivity extends AppCompatActivity {

    private CourseAdapter courseAdapter;
    private organizerViewModel viewModel;
    private int currentUserId;

    // Activity result launcher to handle new course activity results
    private final ActivityResultLauncher<Intent> addCourseResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String courseName = data.getStringExtra("course_name");
                    String courseProfessor = data.getStringExtra("course_professor");
                    String courseDay = data.getStringExtra("course_date");
                    String courseTime = data.getStringExtra("course_time");
                    String courseRoom = data.getStringExtra("course_room");
                    int courseGrade = data.getIntExtra("course_grade", 0);
                    int courseECTS = data.getIntExtra("course_ECTS", 0);
                    boolean isCompleted = data.getBooleanExtra("course_completed", false);

                    // Only insert if all necessary info is available
                    if (courseName != null && courseProfessor != null  && courseDay != null && courseTime != null) {
                        Course newCourse = new Course(currentUserId, courseName, courseProfessor, courseDay, courseTime, courseRoom, courseGrade, courseECTS, isCompleted);
                        Log.d("CourseActivity", "Inserting course: " + newCourse.toString());

                        viewModel.insertCourse(newCourse);
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_main);  //LOAD UI LAYOUT

        //VIEW MODEL SET UP
        viewModel = new ViewModelProvider(this).get(organizerViewModel.class);


        //GET USER ID FROM AN INTENT
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        Log.d("CourseActivity", "Received USER_ID: " + currentUserId);

        viewModel.setCurrentUserId(currentUserId);

        initViews();
    }

    //VIEWS,RECYCLER VIEW, ADAPTER, OBSERVERS
    private void initViews() {

        //CONNECTIONS WITH UI ELEMENTS
        RecyclerView courseRecyclerView = findViewById(R.id.recyclerViewCourses);
        FloatingActionButton addCourseButton = findViewById(R.id.addCourseButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ImageButton filterButton = findViewById(R.id.courseFilterSpinner);

        //ADAPTER
        courseRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        courseAdapter = new CourseAdapter();
        courseRecyclerView.setAdapter(courseAdapter);

        //OBSERVE COURSES FOR CURRENT USER
        viewModel.getCoursesByUser(currentUserId).observe(this, courses -> {
            Log.d("CourseActivity", "Courses updated: " + courses.size());
            courseAdapter.setCourses(courses);
        });

        //ADD A COURSE BUTTON LOGIC
        addCourseButton.setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivity.this, NewCourseActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            addCourseResultLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });

        //CLICK ON A LESSON- POPUP LOGIC
        courseAdapter.setOnItemClickListener(course -> showCoursePopup(course, courseRecyclerView));

        //SPINNER SETUP
        filterButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(CourseActivity.this, filterButton);
            popup.getMenu().add("All");
            popup.getMenu().add("Active");
            popup.getMenu().add("Completed");

            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                switch (title) {
                    case "All":
                        viewModel.getCoursesByUser(currentUserId).observe(CourseActivity.this, courses -> {
                            courseAdapter.setCourses(courses);
                        });
                        break;
                    case "Active":
                        viewModel.getActiveCoursesByUser().observe(CourseActivity.this, courses -> {
                            courseAdapter.setCourses(courses);
                        });
                        break;
                    case "Completed":
                        viewModel.getCompletedCoursesByUser().observe(CourseActivity.this, courses -> {
                            courseAdapter.setCourses(courses);
                        });
                        break;
                }
                return true;
            });

            popup.show();
        });




        //BOTTOM NAVIGATION
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_tasks) {
                Intent intent = new Intent(CourseActivity.this, TaskActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.navigation_courses) {
                return true;
            } else if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(CourseActivity.this, MainActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });
    }

    //DISPLAY COURSE INFO- POPUP WINDOW
    private void showCoursePopup(Course course, RecyclerView parentView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_course_detail, null);

        View dimBackground = findViewById(R.id.dimBackground);
        dimBackground.setVisibility(View.VISIBLE);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);

        //BIND VIEWS
        EditText title = popupView.findViewById(R.id.detailCourseTitle);
        EditText professor = popupView.findViewById(R.id.detailCourseProfessor);
        EditText day = popupView.findViewById(R.id.detailCourseDay);
        EditText time = popupView.findViewById(R.id.detailCourseTime);
        EditText room = popupView.findViewById(R.id.detailCourseRoom);
        EditText grade = popupView.findViewById(R.id.detailCourseGrade);
        EditText ects = popupView.findViewById(R.id.detailCourseECTS);
        CheckBox completedCheckbox = popupView.findViewById(R.id.checkCompleted);

        Button editButton = popupView.findViewById(R.id.btnEdit);
        Button deleteButton = popupView.findViewById(R.id.btnDelete);
        Button saveButton = popupView.findViewById(R.id.btnSave);

        //POPULATE FIELDS
        title.setText(course.getName());
        professor.setText(course.getProfessor());
        day.setText(course.getDay());
        time.setText(course.getTime());
        room.setText(course.getRoom());
        grade.setText(String.valueOf(course.getGrade()));
        ects.setText(String.valueOf(course.getECTS()));
        completedCheckbox.setChecked(course.isCompleted());

        //DISABLE EDITING INITIALLY
        setFieldsEnabled(false, title, professor, day, time, room, grade, ects);
        completedCheckbox.setEnabled(false);
        saveButton.setVisibility(View.GONE);

        //ENABLE EDITING WITH EDIT BUTTON
        editButton.setOnClickListener(v -> {
            setFieldsEnabled(true, title, professor, day, time, room, grade, ects);
            completedCheckbox.setEnabled(true);
            saveButton.setVisibility(View.VISIBLE);
        });

        //SAVE CHANGES WITH CHANGE BUTTON
        saveButton.setOnClickListener(v -> {
            course.setName(title.getText().toString());
            course.setProfessor(professor.getText().toString());
            course.setDay(day.getText().toString());
            course.setTime(time.getText().toString());
            course.setRoom(room.getText().toString());
            course.setGrade(Integer.parseInt(grade.getText().toString()));
            course.setECTS(Integer.parseInt(ects.getText().toString()));
            course.setCompleted(completedCheckbox.isChecked());

            viewModel.updateCourse(course);
            popupWindow.dismiss();
            dimBackground.setVisibility(View.GONE);
            courseAdapter.notifyDataSetChanged();
        });

        //DELETE COURSE LOGIC
        deleteButton.setOnClickListener(v -> new AlertDialog.Builder(CourseActivity.this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.deleteCourse(course);
                    popupWindow.dismiss();
                    dimBackground.setVisibility(View.GONE);
                })
                .setNegativeButton("No", null)
                .show());

        //REMOVE DIM WHEN POPUP APPEARS
        popupWindow.setOnDismissListener(() -> dimBackground.setVisibility(View.GONE));
    }

    //ENABLE-DISABLE MULTIPLE WINDOWS AT ONCE
    private void setFieldsEnabled(boolean enabled, EditText... fields) {
        for (EditText field : fields) {
            field.setEnabled(enabled);
        }
    }
}
