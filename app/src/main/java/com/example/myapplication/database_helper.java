package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class database_helper extends SQLiteOpenHelper {


    public static final String note_record = "create table note("
            + "note_id integer primary key autoincrement,"
            + "note_name text,"
            + "button_id integer,"
            + "note_time text)";
    //create table note(note_name text primary key autoincrement,note_time text)
    private Context mcontext;

    public database_helper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mcontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase s) {
            s.execSQL(note_record);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
