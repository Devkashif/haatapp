package com.haatapp.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbhealper extends SQLiteOpenHelper {

    public dbhealper(Context context) {
        super(context, "shopjinu.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contact(id INTEGER primary key autoincrement,done varchar(255))");

    }
   public void insert()
    {
        SQLiteDatabase sqLiteOpenHelper=this.getWritableDatabase() ;
        sqLiteOpenHelper.execSQL("insert  into contact(done) values('yes')");
    }
    public boolean getContact()
    {
        SQLiteDatabase sqLiteOpenHelper=this.getWritableDatabase() ;
      Cursor cursor
        =sqLiteOpenHelper.rawQuery("select * from contact",null);
   if(cursor.moveToNext())
   {
       return  true;
   }
   else
   {
       return  false;
   }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
