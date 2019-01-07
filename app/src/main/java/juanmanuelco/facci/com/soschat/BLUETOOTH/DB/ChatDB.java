package juanmanuelco.facci.com.soschat.BLUETOOTH.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Usuario;

public class ChatDB {
    private MainDB database;

    public static final String TABLE_NAME = "Chat";
    public static final String ID = "ID";
    public static final String FECHA = "FECHA";
    public static final String ESTADO = "USERNAME";

    public ChatDB(Context context) { this.database = new MainDB(context); }

    public ChatDB(MainDB db) { this.database = db; }

    /* Inner class that defines the table contents */
    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " ("+ID+" TEXT primary key, "+FECHA+" text not null, "+ESTADO+" int not null);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Chat chat){
        try{
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

        }
        return false;
    }

    public boolean Delete(Usuario usuario){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            String Where  = ID+" Like " + usuario.getMac();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }

    public List<Chat>  getAllChat(){
        List<Chat> list = new ArrayList<Chat>();
        String[] allColumns = {ID,FECHA,ESTADO};
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

}
