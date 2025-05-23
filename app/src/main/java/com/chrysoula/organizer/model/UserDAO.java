package com.chrysoula.organizer.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDAO {

    @Insert
    long insert(User user);   //insert a user in the database and return the id

    @Query("DELETE FROM users WHERE id = :id")
    void deleteUserById(int id);


    @Query("DELETE FROM users")
    void deleteAllUsers();


    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsernameBlocking(String username);


    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    LiveData<User> login(String username, String password);  //Returns the user with the specific username and password

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    @Query("UPDATE users SET password = :newPassword WHERE id = :id")
    void updatePassword(int id, String newPassword);

    @Query("SELECT id FROM users WHERE username = :username LIMIT 1")
    LiveData<Integer> getUserIdByUsername(String username);

}
