package com.example.libreta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ListsDatabase extends SQLiteOpenHelper {

    private static final String DB_Name = "Lists.db";
    private static final String DB_table = "Lists";

    //Columns
    private static final String ID = "ID";
    private static final String Task = "TASK";
    private static final String Checked = "CHECKED";

    private static final String Create_table = "CREATE TABLE " + DB_table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Task + " TEXT, "
            + Checked + "  TEXT )";

    // private static final String Create_table = "CREATE TABLE " + DB_table +
    //       " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
    //     + Task + " TEXT, "
    //   + Checked + "TEXT "
    // + ")";

    public ListsDatabase(Context context) {
        super(context, DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_table);
        String sql =
                "INSERT or replace INTO Lists (TASK,CHECKED) VALUES ('hola','disabled')";

        ContentValues insertValues = new ContentValues();
        insertValues.put("TASK", "hola");
        insertValues.put("CHECKED", "Disable");
        db.insert("Lists", null, insertValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_table);

        onCreate(db);
    }

    public boolean insertData(String task) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Checked, "Disable");
        contentValues.put(Task, task);


        long result = db.insert(DB_table, null, contentValues);

        return result != -1;
    }

    public Cursor getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT name FROM sqlite_temp_master WHERE type='table' AND name!='android_metadata' order by name";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor viewList() {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT TASK FROM " + DB_table;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public boolean deleteTask(String list, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + list + " WHERE TASK='" + task + "'";
        Log.w("myApp", "TamañoTasks2: " + query);
        //Cursor cursor = db.rawQuery(query, null);


        return db.delete(list, "TASK=?", new String[]{task}) > 0;
    }

    public Cursor deleteList(String list) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DROP TABLE " + list;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }
}
