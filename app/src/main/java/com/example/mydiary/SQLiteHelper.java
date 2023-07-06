package com.example.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase database) {
        String createTableQuery = "CREATE TABLE diary ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, "
                + "date TEXT, "
                + "author TEXT, "
                + "content TEXT, "
                + "picture TEXT)";
        database.execSQL(createTableQuery);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS diary";
        database.execSQL(dropTableQuery);
        onCreate(database);
    }

    public boolean insert(String title, String date, String author, String content) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("date", date);
        values.put("author", author);
        values.put("content", content);
        long insertRows = database.insert("diary", null, values);
        values.clear();
//        database.close();
        if (insertRows >= 0)
            return true;
        return false;
    }

    public boolean delete(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        int deleteRows = database.delete("diary", "id = ?", new String[]{id});
        database.close();
        if (deleteRows > 0)
            return true;
        return false;
    }

    public boolean update(String id, String title, String date, String author, String content) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("date", date);
        values.put("author", author);
        values.put("content", content);
        int updateRows = database.update("diary", values, "id = ?", new String[]{id});
        database.close();
        if (updateRows > 0)
            return true;
        return false;
    }

    public Map<String, String> getData(String _id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM diary WHERE id = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{_id});
        Map<String, String> map = new HashMap<>();
        if (cursor.moveToFirst()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            String author = cursor.getString(3);
            String content = cursor.getString(4);
            map.put("id", id);
            map.put("title", title);
            map.put("date", date);
            map.put("author", author);
            map.put("content", content);
        }
        cursor.close();
        database.close();
        return map;
    }

    public List<Map<String, String>> getAll() {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM diary";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<Map<String, String>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String date = cursor.getString(2);
                String author = cursor.getString(3);
                String content = cursor.getString(4);
                map.put("id", id);
                map.put("title", title);
                map.put("date", date);
                map.put("author", author);
                map.put("content", content);
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }
}
