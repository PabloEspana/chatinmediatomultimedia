package com.android.wondercom.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.android.wondercom.Entities.Grupo;
import com.android.wondercom.Entities.Usuario;

import java.util.ArrayList;
import java.util.List;

public class GrupoDB {
    private MainDB database;
    public static final String TABLE_NAME = "Grupos";

    public GrupoDB(Context context) {
        this.database = new MainDB(context);
    }

    public static abstract class ElementEntry implements BaseColumns {

        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " (Group_ID text not null, Group_Name text not null, Group_Date timestamp not null, " +
                "primary key(Group_ID) );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Grupo grupo){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Group_ID", grupo.getID());
            values.put("Group_Name", grupo.getName());
            values.put("Group_Date", grupo.getDate());
            db.insert(TABLE_NAME,null,values);
            db.close();
            return true;
        }
        catch (Exception ex){

        }
        return false;
    }

    public boolean Delete(Grupo grupo){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            String Where  = "Group_ID Like " + grupo.getID();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }


    public List<Grupo> getAll(){
        List<Grupo> list = new ArrayList<Grupo>();
        String[] Columns = {"Group_ID","Group_Name","Group_Date"};
        Cursor cursor = database.getReadableDatabase().query(
                TABLE_NAME,
                Columns,
                null,
                null,
                null,
                null,
                null
        );

        while (!cursor.moveToNext()) {
            Grupo user = new Grupo(cursor.getString(0),
                    cursor.getString(1),cursor.getLong(2));
            list.add(user);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }

    public List<Usuario> getAllUserFromGroup(Grupo grupo){
        List<Usuario> list = new ArrayList<Usuario>();
        String[] allColumns = {"Member_ID","Member_MAC","Member_Name"};
        Cursor cursor = database.getReadableDatabase().query(
                MiembroDB.TABLE_NAME,
                allColumns,
                "Group_ID Like "+grupo.getID(),
                null,
                null,
                null,
                null
        );

        while (!cursor.moveToNext()) {
            Usuario user = new Usuario(cursor.getString(0), cursor.getString(1));
            list.add(user);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }

    public List<Usuario> getAllMessageFromGroup(Grupo grupo){
        List<Usuario> list = new ArrayList<Usuario>();
        String Query = "Select * From ((Mensajes INNER JOIN Grupos ON Mensaje.Group_ID like Grupos.Group_ID) " +
                "INNER JOIN Miembros ON Mensajes.Msg_MAC_Orig like Miembros.Member_MAC)";

        Cursor cursor = database.getReadableDatabase().rawQuery(Query,null);
        while (!cursor.moveToNext()) {
            Usuario user = new Usuario(cursor.getString(0), cursor.getString(1));
            list.add(user);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }
}
