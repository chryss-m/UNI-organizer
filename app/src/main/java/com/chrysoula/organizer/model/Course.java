package com.chrysoula.organizer.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses",foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE)
)
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private String name;

    private String professor;
    private String day;
    private String time;

    private String room;
    private int grade;
    private int ECTS;

    private boolean completed;

    public Course(int userId,String name, String professor, String day, String time, String room, int grade, int ECTS, boolean completed) {
        this.userId=userId;
        this.name = name;
        this.professor = professor;
        this.day = day;
        this.time = time;
        this.room = room;
        this.grade = grade;
        this.ECTS = ECTS;
        this.completed=completed;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }


    public String getRoom(){
        return room;
    }

    public void setRoom(String room){
        this.room = room;
    }

    public int getGrade(){
        return grade;
    }

    public void setGrade(int grade){
        this.grade = grade;
    }

    public int getECTS(){
        return ECTS;
    }

    public void setECTS(int ECTS) {
        this.ECTS = ECTS;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }



}
