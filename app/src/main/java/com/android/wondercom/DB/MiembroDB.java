package com.android.wondercom.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.android.wondercom.Entities.Miembro;

public class MiembroDB {
    private MainDB database;
    public static final String TABLE_NAME = "Miembro";

    public MiembroDB(Context context) {
        this.database = new MainDB(context);
    }
    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " (Member_ID int primary key autoincrement, Member_MAC text not null, Group_ID text, Member_Name text not null, Member_Date timestamp not null, " +
                "foreign key(Group_ID) references "+GrupoDB.TABLE_NAME +" (Group_ID) ;";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Miembro miembro){
        try{
            return true;
        }
        catch (Exception ex){

        }
        return false;
    }
}
