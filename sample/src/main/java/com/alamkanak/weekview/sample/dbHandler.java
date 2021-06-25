package com.alamkanak.weekview.sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 03/12/2015.
 */
public class dbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;// Database Version
    private static final String DATABASE_NAME = "local";// Database Name

    public dbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tbuser = "CREATE TABLE IF NOT EXISTS user(id VARCHAR, master_id VARCHAR, identificacion VARCHAR, nombre1 VARCHAR, nombre2 VARCHAR, apellido1 VARCHAR, apellido2 VARCHAR)";
        String tbnoti = "CREATE TABLE IF NOT EXISTS noti(id VARCHAR, titulo VARCHAR, descripcion VARCHAR, tipo VARCHAR, fecha VARCHAR, hora VARCHAR, fechaf VARCHAR, horaf VARCHAR, estado INTEGER, ventana VARCHAR,allday INTEGER, notificarme INTEGER)";

        db.execSQL(tbuser);
        db.execSQL(tbnoti);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS noti");// Drop older table if existed
        onCreate(db);// Creating tables again
    }

    public Cursor onCursor(String sql){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        return cursor;
    }

    public void onInsert(String table, ContentValues value){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(table,null,value);
    }

    public void onUpdate(String table,String where,String[] where_val, ContentValues value){
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(table, value, where, where_val);
    }

    public void onDelete(String sql, String where,String[] value){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(sql,where,value);
    }
}