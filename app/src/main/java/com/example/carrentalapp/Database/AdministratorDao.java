package com.example.carrentalapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.carrentalapp.Model.Administrator;

import java.util.List;

@Dao
public interface AdministratorDao {

    @Query("SELECT * FROM Administrator")
    List<Administrator> getAll();

    @Query("SELECT * FROM Administrator WHERE administratorID = :id AND password = :password")
    Administrator findAdministrator(String id, String password);

    @Query("SELECT * FROM Administrator WHERE administratorID = :id")
    Administrator findAdministrator(String id);

    @Query("DELETE FROM Administrator WHERE administratorID >= 0")
    void deleteAll();

    @Query("SELECT * FROM Administrator WHERE administratorID = :administratorID")
    boolean exist(int administratorID);

    @Delete
    void delete(Administrator administrator);

    @Insert
    void insert(Administrator administrator);

    @Update
    void update(Administrator administrator);

}