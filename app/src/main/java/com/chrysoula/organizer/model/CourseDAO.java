package com.chrysoula.organizer.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CourseDAO {

    @Insert
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses WHERE userId = :userId")
    LiveData<List<Course>> getCoursesByUser(int userId);

    @Query("SELECT * FROM courses WHERE userId = :userId AND completed = 0")
    LiveData<List<Course>> getActiveCoursesByUser(int userId);

    @Query("SELECT * FROM courses WHERE userId = :userId AND completed = 1")
    LiveData<List<Course>> getCompletedCoursesByUser(int userId);

    @Query("SELECT COUNT(*) FROM courses WHERE userId = :userId")
    LiveData<Integer> getCourseCountByUser(int userId);

    @Query("SELECT * FROM courses WHERE userId = :userId AND day = :day ORDER BY time ASC")
    LiveData<List<Course>> getCoursesForDayByUser(int userId, String day);


}
