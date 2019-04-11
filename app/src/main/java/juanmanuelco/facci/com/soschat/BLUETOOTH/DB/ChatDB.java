package juanmanuelco.facci.com.soschat.BLUETOOTH.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Mensaje;

public class ChatDB {

    private static MainDB database;

    public static final String TABLE_NAME = "Chat";
    public static final String ID = "ID";
    public static final String FECHA = "FECHA";
    public static final String ESTADO = "ESTADO";

    public ChatDB(Context context) {
        database = new MainDB(context);
    }

    /* Inner class that defines the table contents */
    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " ("+ID+" TEXT primary key, "+FECHA+" text not null, "+ESTADO+" integer not null);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static boolean Insert(Context context, Chat chat){
        try{
            database = new MainDB(context);
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ID, chat.getID());
            values.put(FECHA, chat.getDate());
            values.put(ESTADO, chat.getEstado());
            db.insert(TABLE_NAME,null,values);
            db.close();
            return true;
        }
        catch (Exception ex){
            Log.d("Error",  ex.toString());
        }
        return false;
    }

    public boolean Delete(Chat chat){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            String Where  = ID+" Like " + chat.getID();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }

    public List<Chat>  getAllChat(){
        List<Chat> list = new ArrayList<Chat>();
        String[] allColumns = {ID, FECHA, ESTADO};
        Cursor cursor = database.getReadableDatabase().query(
                TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null
        );

        while (!cursor.moveToNext()) {
            Chat chat = new Chat(cursor.getString(0),cursor.getString(1),cursor.getInt(2));
            list.add(chat);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }

    public static List<Chat> obtenerConversacionesActivas(Context context){
        List<Chat> list = new ArrayList<Chat>();
        try{
            MainDB database = new MainDB(context);
            SQLiteDatabase db = database.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM Chat WHERE ESTADO = 1", null);
            while (cursor.moveToNext()) {
                Chat chats = new Chat(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getInt(2));
                Log.i("Conversaciones", cursor.toString());
                list.add(chats);
            }
            cursor.close();
            database.getReadableDatabase().close();
            Log.i("Correcto", list.toString());
            return list;
        }catch (Exception ex){
            Log.e("Error", ex.toString());
            return list;
        }
    }


}
