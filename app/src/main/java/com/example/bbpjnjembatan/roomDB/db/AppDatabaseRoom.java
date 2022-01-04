package com.example.bbpjnjembatan.roomDB.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.bbpjnjembatan.roomDB.dao.UserDao;
import com.example.bbpjnjembatan.roomDB.entity.User;

@Database(entities = {User.class}, exportSchema = false, version = 1)
public abstract class AppDatabaseRoom extends RoomDatabase {
    public abstract UserDao userDao();
}
