package com.chrysoula.organizer.UI;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.viewmodel.organizerViewModel;

import java.text.BreakIterator;

public class TaskViewHolder extends RecyclerView.ViewHolder
{


    CheckBox checkboxView;
    TextView taskTitleView, taskDeadlineView,taskDescriptionView,taskCourseView;

    ImageButton taskOptions;


    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        checkboxView=itemView.findViewById(R.id.taskCheckBox);
        taskTitleView=itemView.findViewById(R.id.taskTitle);
        taskDeadlineView = itemView.findViewById(R.id.taskDeadline);
        taskOptions = itemView.findViewById(R.id.taskOptions);
        taskDescriptionView=itemView.findViewById(R.id.taskDescription);

    }

    public void bind(Task task, organizerViewModel viewModel, Context context) {
        taskTitleView.setText(task.getTitle());
        checkboxView.setChecked(task.isStatus());
        taskDescriptionView.setText(task.getDescription());

        taskDeadlineView.setText(
                new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        .format(task.getDeadline())
        );

        checkboxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setStatus(isChecked);
            viewModel.updateTask(task);
        });

        taskOptions.setOnClickListener(v -> {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    android.content.Intent intent = new android.content.Intent(context, EditTaskActivity.class);
                    intent.putExtra("task_id", task.getId());
                    intent.putExtra("task_title", task.getTitle());
                    intent.putExtra("task_description", task.getDescription());
                    intent.putExtra("task_deadline", task.getDeadline().getTime());
                    intent.putExtra("course_id", task.getCourseId());
                    context.startActivity(intent);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    View dialogView = android.view.LayoutInflater.from(context).inflate(R.layout.delete_task, null);

                    androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setView(dialogView)
                            .setCancelable(false)
                            .create();

                    dialogView.findViewById(R.id.buttonCancelTask).setOnClickListener(v1 -> dialog.dismiss());

                    dialogView.findViewById(R.id.buttonDeleteTask).setOnClickListener(v12 -> {
                        viewModel.deleteTask(task);
                        dialog.dismiss();
                    });

                    dialog.show();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

}
