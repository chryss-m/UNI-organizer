package com.chrysoula.organizer.model;
import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Course.class, Task.class,User.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {



    //returns the DAO which provides the methods to access the entities
    public abstract CourseDAO courseDAO();
    public abstract TaskDAO taskDAO();
    public abstract UserDAO userDAO();


    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "organizer_uni3_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}


/*
To use the database in your application, you need to interact with the database through DAOs.

    AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

 */