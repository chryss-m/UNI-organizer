package com.chrysoula.organizer.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.chrysoula.organizer.model.Course;

import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.model.AppDatabase;
import com.chrysoula.organizer.model.CourseDAO;
import com.chrysoula.organizer.model.TaskDAO;
import com.chrysoula.organizer.model.User;
import com.chrysoula.organizer.model.UserDAO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//it takes over access to the DAOs
public class organizerRepository {

    private final  CourseDAO courseDAO;        //DAO for accessing courses
    private final  TaskDAO taskDAO;  //DAO for accessing tasks

    private final UserDAO userDAO;  //DAO for accessing users

    private final  ExecutorService executorService;    //to run DB operations in background threads

    //Constructor for initialization
    public organizerRepository(Application application){

        AppDatabase database = AppDatabase.getDatabase((application));
        courseDAO= database.courseDAO();   //get CourseDao from db
        taskDAO=database.taskDAO();         //get TaskDao from db
        userDAO= database.userDAO();
        executorService = Executors.newFixedThreadPool(2);  //thread pool to handle background operations

    }

    //COURSES
    public void insertCourse(Course course) {
        executorService.execute(() -> {
            try {
                courseDAO.insert(course);
                Log.d("Repository", "Insert successful");
            } catch (Exception e) {
                Log.e("Repository", "Insert failed", e);
            }
        });

    }


    public void updateCourse(Course course) {
        executorService.execute(() -> courseDAO.update(course));
    }




    public void deleteCourse(Course course) {
        executorService.execute(() -> courseDAO.delete(course));
    }

    public LiveData<List<Course>> getCoursesByUser(int userId) {
        return courseDAO.getCoursesByUser(userId);
    }

    public LiveData<List<Course>> getActiveCoursesByUser(int userId) {
        return courseDAO.getActiveCoursesByUser(userId);
    }

    public LiveData<List<Course>> getCompletedCoursesByUser(int userId) {
        return courseDAO.getCompletedCoursesByUser(userId);
    }

    public LiveData<Integer> getCourseCountByUser(int userId) {
        return courseDAO.getCourseCountByUser(userId);
    }

    public LiveData<List<Course>> getCoursesForDayByUser(int userId, String day) {
        return courseDAO.getCoursesForDayByUser(userId, day);
    }



    //TASKS
    public void insertTask(Task task) {
        executorService.execute(() -> taskDAO.insert(task));
    }

    public LiveData<List<Task>> getTasksForCourse(int courseId) {
        return taskDAO.getTasksForCourse(courseId);
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDAO.update(task));
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDAO.delete(task));
    }



    public LiveData<List<Task>>getIncompleteTasksByUser(int userId){
        return taskDAO.getIncompleteTasksByUser(userId);
    }

    public LiveData<List<Task>> getTasksByUser(int userId) {
        return taskDAO.getTasksByUser(userId);
    }

    public LiveData<List<Task>> getTasksOrderedByDeadlineForUser(int userId) {
        return taskDAO.getTasksOrderedByDeadlineForUser(userId);
    }

    public LiveData<List<Task>> getTasksForCourseByUser(int userId, int courseId) {
        return taskDAO.getTasksForCourseByUser(userId, courseId);
    }



    //USERS
    public void insertUser(User user) {
        executorService.execute(() -> userDAO.insert(user));
    }

    public interface UserRegistrationCallback {
        void onResult(boolean success, String message);
    }

    public void registerUser(String username, String password, UserRegistrationCallback callback) {
        executorService.execute(() -> {
            try {
                User existingUser = userDAO.getUserByUsernameBlocking(username);
                if (existingUser != null) {
                    Log.d("RegisterUser", "Username already exists: " + username);
                    callback.onResult(false, "This username already exists. Try something else.");
                } else {
                    long userId = userDAO.insert(new User(username, password));

                    if (userId > 0) {
                        Log.d("RegisterUser", "User created successfully with id = " + userId);

                        Course course = new Course((int) userId, "Μάθημα", "Καθηγητής", "Δευτέρα", "10:00", "Αίθουσα", 0, 6, false);
                        courseDAO.insert(course);

                        Log.d("RegisterUser", "Default course created for userId = " + userId);

                        callback.onResult(true, "Successful Registration!");
                    } else {
                        Log.e("RegisterUser", "Failed to insert user.");
                        callback.onResult(false, "Failed to register user.");
                    }
                }
            } catch (Exception e) {
                Log.e("RegisterUser", "Failure: " + e.getMessage());
                callback.onResult(false, "Failure: " + e.getMessage());
            }
        });
    }




    public LiveData<User> login(String username, String password) {
        return userDAO.login(username, password);
    }

    public LiveData<User> getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public void deleteUserById(int id) {
        executorService.execute(() -> userDAO.deleteUserById(id));
    }

    public void deleteAllUsers() {
        executorService.execute(userDAO::deleteAllUsers);
    }

    public void updateUserPassword(int id, String newPassword) {
        executorService.execute(() -> userDAO.updatePassword(id, newPassword));
    }

    public LiveData<Integer> getUserIdByUsername(String username) {
        return userDAO.getUserIdByUsername(username);
    }




}
