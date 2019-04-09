package juanmanuelco.facci.com.soschat.BLUETOOTH.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Usuario;

public class UsuarioDB {
    private MainDB database;

    public static final String TABLE_NAME = "Usuario";
    public static final String ID = "ID";
    public static final String MAC_BT = "MAC_BT";
    public static final String USERNAME = "USERNAME";

    public UsuarioDB(Context context) {
        this.database = new MainDB(context);
    }

    public UsuarioDB(MainDB db) {
        this.database = db;
    }

    /* Inner class that defines the table contents */
    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " ("+ID+" integer primary key autoincrement, "+MAC_BT+" text not null, "+USERNAME+" text not null);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Usuario usuario){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MAC_BT, usuario.getMac());
            values.put(USERNAME, usuario.getUserName());
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
            String Where  = MAC_BT+" Like " + usuario.getMac();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }

    public Usuario GetUserByMacBT(String MAC){
        Usuario user = null;
        String[] allColumns = {ID,MAC_BT,USERNAME};
        Cursor cursor = database.getReadableDatabase().query(
                TABLE_NAME,
                allColumns,
                MAC_BT +" Like '"+MAC+"'",
                null,
                null,
                null,
                null
        );
        while (!cursor.moveToNext()) {
            user = new Usuario(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            break;
        }
        return user;
    }

    public List<Usuario> getAll(){
        List<Usuario> list = new ArrayList<Usuario>();
        String[] allColumns = {ID,MAC_BT,USERNAME};
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
            Usuario user = new Usuario(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            list.add(user);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }
}

