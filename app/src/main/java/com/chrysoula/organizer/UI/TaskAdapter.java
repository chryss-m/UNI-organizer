package com.chrysoula.organizer.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.viewmodel.organizerViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final organizerViewModel viewModel;

    private final View dimBackground;

    public TaskAdapter(Context context, organizerViewModel viewModel, View dimBackground) {
        this.context = context;
        this.viewModel = viewModel;
        this.dimBackground = dimBackground;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitleView.setText(task.getTitle());
        holder.checkboxView.setChecked(task.isStatus());
        holder.taskDeadlineView.setText(dateFormat.format(task.getDeadline()));
        holder.taskDescriptionView.setText(task.getDescription());


        holder.checkboxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setStatus(isChecked);
            viewModel.updateTask(task);
        });

        holder.taskOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    Intent intent = new Intent(context, EditTaskActivity.class);
                    intent.putExtra("task_id", task.getId());
                    intent.putExtra("task_title", task.getTitle());
                    intent.putExtra("task_description", task.getDescription());
                    intent.putExtra("task_deadline", task.getDeadline().getTime());
                    intent.putExtra("course_id", task.getCourseId());
                    intent.putExtra("user_id", ((TaskActivity) context).currentUserId);
                    context.startActivity(intent);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_task, null);

                    androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                            .setView(dialogView)
                            .setCancelable(false)
                            .create();


                    dimBackground.setVisibility(View.VISIBLE);

                    dialog.setOnDismissListener(dialogInterface -> dimBackground.setVisibility(View.GONE));

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

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public void setTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }


}
