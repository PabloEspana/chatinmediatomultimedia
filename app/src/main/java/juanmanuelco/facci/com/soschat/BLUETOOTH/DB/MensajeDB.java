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
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Usuario;

public class MensajeDB {

    private static MainDB database;

    public static final String TABLE_NAME = "MensajeI";
    public static final String ID_MENSAJE = "ID_MENSAJE";
    public static final String ID_CHAT = "ID_CHAT";
    public static final String FECHA = "FECHA";
    public static final String TIPO = "TIPO";
    public static final String CONTENT = "CONTENT";
    public static final String TEMPO = "TEMPO";
    public static final String ELECTURA = "ESTADO_LECTURA";
    public static final String EENVIO = "ESTADO_ENVIO";
    public static final String MAC_ORIGEN = "MAC_ORIGEN";
    public static final String MAC_DESTINO= "MAC_DESTINO";
    public static final String ESMIO = "ESMIO";

    public MensajeDB(Context context) { this.database = new MainDB(context); }

    public MensajeDB(MainDB db) { this.database = db; }

    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE= "Create table if not exists " +TABLE_NAME+ " (" +
                    ID_MENSAJE+ " text primary key not null, " +
                    ID_CHAT+ " text not null, " +
                    FECHA+ " text not null, " +
                    TIPO+ " text not null, " +
                    CONTENT+ " blob not null, " +
                    TEMPO+ " integer not null, " +
                    ELECTURA+ " integer not null, " +
                    EENVIO+ " integer not null, " +
                    MAC_ORIGEN+ " text not null, " +
                    MAC_DESTINO+ " text not null, " +
                    ESMIO+ " integer not null, " +
                    "foreign key("+ID_CHAT+") references "+ChatDB.TABLE_NAME+"("+ChatDB.ID+") " +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static boolean Insert(Context context, Mensaje mensaje){
        try{
            database = new MainDB(context);
            SQLiteDatabase db = database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ID_MENSAJE, "msg-"+(int)(10000000 * Math.random()));
            values.put(ID_CHAT, mensaje.getID_CHAT());
            values.put(FECHA, mensaje.getDate());
            values.put(TIPO, mensaje.getType());
            values.put(CONTENT, mensaje.getContent());
            values.put(TEMPO, mensaje.getTime());
            values.put(ELECTURA, mensaje.EstaoLectura());
            values.put(EENVIO, mensaje.EstaoEnvio());
            values.put(MAC_ORIGEN, mensaje.getMAC_ORIGEN());
            values.put(MAC_DESTINO, mensaje.getMAC_DESTINO());
            values.put(ESMIO, mensaje.EsMio());
            db.insert(TABLE_NAME,null,values);

            // Actualiza estado de chat a activo
            ContentValues value_update = new ContentValues();
            value_update.put("ESTADO", 1);
            db.update("Chat", value_update, "ID = ?",  new String[]{mensaje.getID_CHAT()});

            db.close();
            return true;
        }
        catch (Exception ex){
            Log.e("Error", ex.toString());
        }
        return false;
    }


    public static List<Mensaje> getAllMessages(Context context, String id_chat){
        List<Mensaje> list = new ArrayList<Mensaje>();
        try{
            MainDB database = new MainDB(context);
            SQLiteDatabase db = database.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM MensajeI WHERE ID_CHAT = ? ORDER BY date(FECHA)", new String[] {id_chat});
            while (cursor.moveToNext()) {
                Mensaje mensaje = new Mensaje(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10));
                Log.i("Contenido", cursor.toString());
                list.add(mensaje);
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

    public static List<Mensaje> getAllNotSendMessages(Context context){
        List<Mensaje> list = new ArrayList<Mensaje>();
        try{
            MainDB database = new MainDB(context);
            SQLiteDatabase db = database.getWritableDatabase();
            //Cursor cursor = db.rawQuery("SELECT * FROM MensajeI WHERE ID_CHAT = ? AND ESTADO_ENVIO = 0 ORDER BY date(FECHA)", new String[] {id_chat});
            Cursor cursor = db.rawQuery("SELECT * FROM MensajeI WHERE ESTADO_ENVIO = 0 ORDER BY date(FECHA)", null);
            while (cursor.moveToNext()) {
                Mensaje mensaje = new Mensaje(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getInt(6),
                        cursor.getInt(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getInt(10));
                list.add(mensaje);
            }
            cursor.close();
            database.getReadableDatabase().close();
            return list;
        }catch (Exception ex){
            Log.e("Error", ex.toString());
            return list;
        }
    }

    public static Cursor verMensajes(Context context, String id){
        MainDB database = new MainDB(context);
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM MensajeI WHERE ID_CHAT = ? ORDER BY date(FECHA) DESC Limit 1 ", new String[] {id});
        return  res;
    }

    public static boolean eliminarDuplicado(Context context, String id_mmsg){
        try{
            database = new MainDB(context);
            SQLiteDatabase db = database.getWritableDatabase();
            db.delete("MensajeI", "ID_MENSAJE = ?",  new String[]{id_mmsg});
            db.close();
            return true;
        }
        catch (Exception ex){
            Log.e("Error", ex.toString());
        }
        return false;
    }

    public boolean Delete(Mensaje mensaje){
        try{
            SQLiteDatabase db = database.getWritableDatabase();
            String Where  = ID_MENSAJE+" = " + mensaje.getID_MESSAGE();
            db.delete(TABLE_NAME,Where,null);
            db.close();
        }
        catch (Exception ex){

        }
        return false;
    }

}
