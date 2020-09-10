package com.ziq.baselib.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version =  1 , exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}
