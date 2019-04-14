package com.example.libreta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ListsDatabase extends SQLiteOpenHelper {

    private static final String DB_Name = "Lists.db";
    private String DB_table = "Lists";
    //Columns
    private static final String ID = "ID";
    private static final String Task = "TASK";
    private static final String Checked = "CHECKED";


    public ListsDatabase(Context context) {
        super(context, DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_table);

    }

    public void createTable(SQLiteDatabase db) {
        String Create_table = "CREATE TABLE " + DB_table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Task + " TEXT, "
                + Checked + "  TEXT )";
        db.execSQL(Create_table);
    }

    public boolean existTable() {
        Boolean res = false;
        Cursor cursor = getLists();
        while (cursor.moveToNext()) {
            if (cursor.getString(0).contains(DB_table)) {
                res = true;
                break;
            }
        }
        cursor.close();
        return res;
    }

    public boolean insertData(String task) {
        Boolean aux = false;
        SQLiteDatabase db = this.getWritableDatabase();

        setTableName();
        //if table not exist we need to create the table
        if (!existTable()) {
            createTable(this.getWritableDatabase());
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(Checked, "Disable");
        contentValues.put(Task, task);


        long result = db.insert(DB_table, null, contentValues);

        return result != -1;
    }

    public Cursor getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata' AND name!='sqlite_sequence' order by name";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor viewList() {

        Boolean aux = false;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT TASK,CHECKED FROM " + DB_table, null);
    }

    public boolean deleteList(String list) {
        boolean res = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = getLists();
        while (cursor.moveToNext()) {
            if (cursor.getString(0).contains(list)) {
                res = true;
                db.execSQL("DROP TABLE IF EXISTS " + list);
                break;
            }
        }
        cursor.close();
        return res;

    }

    public boolean deleteTask(String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DB_table, "TASK=?", new String[]{task}) > 0;
    }

    public void updateTask(String list, String task, boolean checked) {

        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL;

        if (checked) {
            strSQL = "UPDATE " + DB_table + " SET CHECKED = 'Enable' WHERE TASK = '" + task + "'";
        } else {
            strSQL = "UPDATE " + DB_table + " SET CHECKED = 'Disable' WHERE TASK = '" + task + "'";
        }
        db.execSQL(strSQL);

    }

    public boolean taskExistOnList(String task) {
        boolean res = true;
        Log.w("myApp", "El nombre de la lista en el existtable es:" + DB_table);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String sql = "SELECT TASK FROM '" + DB_table + "' WHERE TASK='" + task + "'";
        cursor = db.rawQuery(sql, null);

        if (cursor.getCount() == 0) {
            res = false;
        }
        cursor.close();
        return res;
    }

    public void setTableName() {
        ListEditor le = new ListEditor();
        DB_table = le.getListName();

    }

    public void changeTableNameOnDB(String name) {
        Log.w("myApp", "El nombre de la tabla era:" + DB_table);
        Log.w("myApp", "El nombre por el que se quiere cambiar la tabla es:" + name);
        SQLiteDatabase db = this.getWritableDatabase();
        if (!existTable()) {
            createTable(db);
            //db.execSQL("ALTER TABLE " + DB_table + " RENAME TO " + name);
        }
        db.execSQL("ALTER TABLE " + DB_table + " RENAME TO " + name);
        setTableName();
        Log.w("myApp", "El nombre de la lista es:" + DB_table);
    }
}
