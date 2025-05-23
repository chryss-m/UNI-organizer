package com.chrysoula.organizer.UI;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chrysoula.organizer.R;
import com.chrysoula.organizer.model.Course;

public class CourseViewHolder extends RecyclerView.ViewHolder {
    private final TextView courseTitle;
    private final TextView courseECTS;

    public CourseViewHolder(@NonNull View itemView) {
        super(itemView);
        courseTitle = itemView.findViewById(R.id.courseTitle);
        courseECTS = itemView.findViewById(R.id.courseECTS);
    }

    public void bind(Course course, CourseAdapter.OnItemClickListener listener) {
        courseTitle.setText(course.getName());
        courseECTS.setText("ECTS: " + course.getECTS());

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(course);
            }
        });
    }
}
