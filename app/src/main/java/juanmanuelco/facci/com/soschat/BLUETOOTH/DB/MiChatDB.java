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

public class MiChatDB {
    private MainDB database;

    public static final String TABLE_NAME = "MiChat";
    public static final String ID = "ID";
    public static final String ID_CHAT = "ID_CHAT";
    public static final String ID_USER = "ID_USER";

    public MiChatDB(Context context) { this.database = new MainDB(context); }

    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " ("+ID+" int primary key autoincrement, "+ID_CHAT+" text not null, "+ID_USER+" int not null);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Chat chat, Usuario user){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ID_CHAT, chat.getID());
            values.put(ID_USER, user.getID());
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
            String Where  = ID+" Like " + usuario.getID();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }

    public List<Chat> getAlMyChat(String MAC){
        Usuario user = new UsuarioDB(database).GetUserByMacBT(MAC);
        List<Chat> list = new ArrayList<Chat>();
        String Query = "Select "+ChatDB.TABLE_NAME+"."+ChatDB.ID+" AS id, "+ChatDB.TABLE_NAME+"."+ChatDB.FECHA+
                " AS fecha, "+ChatDB.TABLE_NAME+"."+ChatDB.ESTADO+" as estado "+
                "From "+TABLE_NAME+" INNER JOIN "+ChatDB.TABLE_NAME+" ON "+TABLE_NAME+"."+ID_CHAT+
                " like "+ ChatDB.TABLE_NAME+"."+ChatDB.ID+" WHERE "+TABLE_NAME+"."+ID_USER+" = "+user.getID();
        Cursor cursor = database.getReadableDatabase().rawQuery(Query,null);
        while (!cursor.moveToNext()) {
            Chat chat = new Chat(cursor.getString(0),cursor.getString(1),cursor.getInt(2));
            list.add(chat);
        }
        cursor.close();
        database.getReadableDatabase().close();

        return list;
    }
}
