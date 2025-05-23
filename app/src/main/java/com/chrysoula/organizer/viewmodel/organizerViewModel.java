package com.chrysoula.organizer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.chrysoula.organizer.model.Course;
import com.chrysoula.organizer.model.Task;
import com.chrysoula.organizer.model.User;
import com.chrysoula.organizer.repository.organizerRepository;

import java.util.List;

public class organizerViewModel extends AndroidViewModel {

    private final organizerRepository repository;
    private int currentUserId = -1;

    public organizerViewModel(@NonNull Application application) {
        super(application);
        repository = new organizerRepository(application);
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    // COURSES

    public LiveData<List<Course>> getCoursesByUser(int userId) {
        return repository.getCoursesByUser(userId);
    }

    public void insertCourse(Course course) {
        repository.insertCourse(course);
    }

    public void updateCourse(Course course) {
        repository.updateCourse(course);
    }

    public void deleteCourse(Course course) {
        repository.deleteCourse(course);
    }

    public LiveData<List<Course>> getActiveCoursesByUser() {
        return repository.getActiveCoursesByUser(currentUserId);
    }

    public LiveData<List<Course>> getCompletedCoursesByUser() {
        return repository.getCompletedCoursesByUser(currentUserId);
    }

    public LiveData<Integer> getCourseCountByUser() {
        return repository.getCourseCountByUser(currentUserId);
    }

    public LiveData<List<Course>> getCoursesForDayByUser(int userId, String day) {
        return repository.getCoursesForDayByUser(userId, day);
    }


    public LiveData<Double> getAverageGradeByUser(int userId) {
        MediatorLiveData<Double> userAverageGrade = new MediatorLiveData<>();
        LiveData<List<Course>> userCourses = getCoursesByUser(userId);

        userAverageGrade.addSource(userCourses, courses -> {
            if (courses != null && !courses.isEmpty()) {
                double totalWeighted = 0;
                int totalEcts = 0;
                for (Course c : courses) {
                    if (c.isCompleted()) {
                        totalWeighted += c.getGrade() * c.getECTS();
                        totalEcts += c.getECTS();
                    }
                }
                double avg = (totalEcts == 0) ? 0.0 : totalWeighted / totalEcts;
                userAverageGrade.setValue(avg);
            } else {
                userAverageGrade.setValue(0.0);
            }
        });

        return userAverageGrade;
    }

    // TASKS
    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public void updateTask(Task task) {
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public LiveData<List<Task>> getTasksByUser(int currentUserId) {
        return repository.getTasksByUser(currentUserId);
    }

    public LiveData<List<Task>> getTasksOrderedByDeadlineForUser() {
        return repository.getTasksOrderedByDeadlineForUser(currentUserId);
    }

    public LiveData<List<Task>> getTasksForCourseByUser(int courseId) {
        return repository.getTasksForCourseByUser(currentUserId, courseId);
    }

    public LiveData<List<Task>> getIncompleteTasksByUser(int userId) {
        return repository.getIncompleteTasksByUser(userId);
    }


    // USERS
    public void insertUser(User user) {
        repository.insertUser(user);
    }

    public void registerUser(String username, String password, organizerRepository.UserRegistrationCallback callback) {
        repository.registerUser(username, password, callback);
    }


    public interface LoginCallback {
        void onLoginResult(boolean success, String message, int userId);
    }


    public void loginUser(String username, String password, LoginCallback callback) {
        LiveData<User> userLiveData = repository.login(username, password);
        userLiveData.observeForever(user -> {
            if (user != null) {
                callback.onLoginResult(true, "Login successful", user.getId());
            } else {
                callback.onLoginResult(false, "Invalid username or password", -1);
            }
        });
    }

    public LiveData<User> getUserByUsername(String username) {
        return repository.getUserByUsername(username);
    }

    public void deleteUserById(int id) {
        repository.deleteUserById(id);
    }

    public void deleteAllUsers() {
        repository.deleteAllUsers();
    }

    public void updatePassword(int id, String newPassword) {
        repository.updateUserPassword(id, newPassword);
    }
    public LiveData<Integer> getUserIdByUsername(String username) {
        return repository.getUserIdByUsername(username);
    }


}
