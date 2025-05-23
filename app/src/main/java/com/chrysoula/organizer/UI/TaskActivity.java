package com.chrysoula.organizer.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.viewmodel.organizerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Date;


//manages a list of tasks in the application. Includes RecyclerView, floating action button and a bottom navigator

public class TaskActivity extends AppCompatActivity {

    private TaskAdapter taskAdapter;        //for handling the recyclerview data
    private organizerViewModel viewModel;
    int currentUserId;

    //HANDLES THE RESULT FROM NEW TASK ACTIVITY WHEN A TASK IS ADDED
    private final ActivityResultLauncher<Intent> addTaskResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        String title = intent.getStringExtra("title");
                        String description = intent.getStringExtra("description");
                        long deadlineMillis = intent.getLongExtra("deadline", 0);
                        int courseId = intent.getIntExtra("course_id", -1);

                        Date deadline = new Date(deadlineMillis);

                        Task newTask = new Task(currentUserId, title, description, deadline, courseId, false);


                        viewModel.insertTask(newTask);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main);

        viewModel = new ViewModelProvider(this).get(organizerViewModel.class);


        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        viewModel.setCurrentUserId(currentUserId);


        //INITIALIZE VIES
        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);
        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        View dimBackground = findViewById(R.id.dimBackground);
        ImageButton filterTaskButton = findViewById(R.id.taskFilterSpinner);

        //LAYOUT MANAGER FOR THE RECYCLER VIEW
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //GET THE CURRENT USER'S ID FROM AN INTENT
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        //INITIALIZE VIEW MODEL
        viewModel = new ViewModelProvider(this).get(organizerViewModel.class);
        viewModel.setCurrentUserId(currentUserId);

        //SET UP ADAPTER
        taskAdapter = new TaskAdapter(this, viewModel, dimBackground);
        taskRecyclerView.setAdapter(taskAdapter);

        //OBSERVE AND UPDATE TASK LIST SPECIFIC TO THE CURRENT USER
        viewModel.getTasksByUser(currentUserId).observe(this, tasks -> taskAdapter.setTasks(tasks));

        //ADD A NEW TASK WITH THE BUTTON
        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskActivity.this, NewTaskActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            addTaskResultLauncher.launch(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);

        });




        viewModel.getTasksByUser(currentUserId).observe(this, tasks -> taskAdapter.setTasks(tasks));

        filterTaskButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(TaskActivity.this, v);
            String[] filters = getResources().getStringArray(R.array.task_filter_options);

            for (int i = 0; i < filters.length; i++) {
                popupMenu.getMenu().add(0, i, i, filters[i]);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        viewModel.getTasksByUser(currentUserId).observe(this, tasks -> taskAdapter.setTasks(tasks));
                        return true;
                    case 1:
                        viewModel.getTasksOrderedByDeadlineForUser().observe(this, tasks -> taskAdapter.setTasks(tasks));
                        return true;

                    case 2:
                        viewModel.getIncompleteTasksByUser(currentUserId)
                                .observe(this, tasks -> taskAdapter.setTasks(tasks));
                        return true;

                    default:
                        return false;
                }
            });

            popupMenu.show();
        });



        //BOTTOM NAVIGATION HANDLING
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_courses) {
                Intent intent = new Intent(TaskActivity.this, CourseActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                return true;
            } else if (itemId == R.id.navigation_tasks) {
                return true;
            } else if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

                return true;
            }
            return false;
        });
    }


}