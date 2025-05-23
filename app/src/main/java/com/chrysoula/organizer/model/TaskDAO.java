package com.chrysoula.organizer.model;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface TaskDAO {                  //an interface that manages the room database for tasks

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);     //insert a task in the DB

    @Update
    void update(Task task);             //updates an existing task

    @Delete
    void delete(Task task);             //deletes a task


    @Query("SELECT * FROM tasks WHERE userId = :userId")
    LiveData<List<Task>> getTasksByUser(int userId);


    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY deadline ASC")
    LiveData<List<Task>> getTasksOrderedByDeadlineForUser(int userId);

    @Query("SELECT * FROM tasks WHERE userId = :userId AND courseId = :courseId")
    LiveData<List<Task>> getTasksForCourseByUser(int userId, int courseId);


    @Query("SELECT * FROM tasks WHERE courseId = :courseId")
    LiveData<List<Task>> getTasksForCourse(int courseId);      //returns tasks according to a course


    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = 0")
    LiveData<List<Task>> getIncompleteTasksByUser(int userId);

}
