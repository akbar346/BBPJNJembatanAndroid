package com.example.bbpjnjembatan.roomDB.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bbpjnjembatan.roomDB.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("DELETE FROM t_user WHERE nip = :id")
    void delete(String id);

    @Query("DELETE FROM t_user")
    void deleteAll();

    @Query("SELECT * FROM t_user LIMIT 1")
    User selectData();

    @Query("SELECT * FROM t_user")
    List<User> selectAllData();
}
