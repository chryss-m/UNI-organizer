package com.chrysoula.organizer.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

//entity creation with the name 'tasks', connected with the course by the 'courseId'

@Entity(tableName = "tasks", foreignKeys = {@ForeignKey(entity = Course.class, parentColumns = "id", childColumns = "courseId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE)})
public class Task {

    @PrimaryKey(autoGenerate = true)

    private int id;     //primary key

    private int userId;

    //Class fields
    private String title;  //foreign key
    private String description;

    @TypeConverters({DateConverter.class})
    private Date deadline;
    private int courseId;  //the foreign key

    private boolean status;

    //Constructor to initialize a task object
    public Task(int userId,String title, String description, Date deadline, int courseId,boolean status){
        this.userId=userId;
        this.title=title;
        this.description=description;
        this.deadline=deadline;
        this.courseId=courseId;
        this.status = status;
    }

    //GETTERS & SETTERS
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public boolean isStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status=status;
    }


}
