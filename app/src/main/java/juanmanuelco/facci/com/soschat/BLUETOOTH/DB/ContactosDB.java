package juanmanuelco.facci.com.soschat.BLUETOOTH.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Contacto;

public class ContactosDB {
    private MainDB database;
    public static final String TABLE_NAME = "Contactos";
    public static final String ID = "ID";
    public static final String ID_User = "ID_User";
    public static final String MAC = "MAC";
    public static final String USERNAME = "USERNAME";

    public ContactosDB(Context context) {
        this.database = new MainDB(context);
    }

    public ContactosDB(MainDB db) { this.database = db; }

    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " ("+ID+" int primary key autoincrement, "+ID_User+" int not null, "+USERNAME+" text not null, " +
                MAC+" text not null, "+
                "foreign key("+ID_User+") references "+UsuarioDB.TABLE_NAME +" ("+UsuarioDB.ID+");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public boolean Insert(Contacto contacto){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ID, contacto.getID());
            values.put(ID_User, contacto.getIDUser());
            values.put(USERNAME, contacto.getUsername());
            values.put(MAC, contacto.getMAC());
            db.insert(TABLE_NAME,null,values);
            db.close();
            return true;
        }
        catch (Exception ex){

        }
        return false;
    }

    public boolean Delete(Contacto contacto){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            String Where  = ID+" = " + contacto.getID();
            db.delete(TABLE_NAME,Where,null);
            db.close();
            contacto =  null;

        }
        catch (Exception ex){

        }
        return false;
    }

    public Contacto getContactFromMac(String mac){
        Contacto contact = null;
        String[] allColumns = {ID,ID_User,MAC,USERNAME};
        Cursor cursor = database.getReadableDatabase().query(
                TABLE_NAME,
                allColumns,
                MAC +" Like '"+mac+"'",
                null,
                null,
                null,
                null
        );
        while (!cursor.moveToNext()) {
            contact = new Contacto(cursor.getInt(0),cursor.getInt(1), cursor.getString(2), cursor.getString(3));
            break;
        }
        return contact;
    }

    public List<Contacto> getAll(){
        List<Contacto> list = new ArrayList<Contacto>();
        String[] allColumns = {ID,ID_User,MAC,USERNAME};
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
            Contacto contact = new Contacto(cursor.getInt(0),cursor.getInt(1), cursor.getString(2), cursor.getString(3));
            list.add(contact);
        }
        cursor.close();
        database.getReadableDatabase().close();
        return list;
    }
}
