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

    public static final String TABLE_NAME = "Usuarios";

    public UsuarioDB(Context context) {
        this.database = new MainDB(context);
    }

    /* Inner class that defines the table contents */
    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " (MAC text not null, User_Name text);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Usuario usuario){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("MAC", usuario.getMac());
            values.put("User_Name", usuario.getUserName());
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
            String Where  = "MAC Like " + usuario.getMac();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }


    public List<Usuario> getAll(){
        List<Usuario> list = new ArrayList<Usuario>();
        String[] allColumns = {"MAC","User_Name"};
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
            Usuario user = new Usuario(cursor.getString(0), cursor.getString(1));
            list.add(user);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }
}

