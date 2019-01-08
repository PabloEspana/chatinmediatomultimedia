package juanmanuelco.facci.com.soschat.BLUETOOTH.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MainDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SOSChat.db";

    public MainDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> sentencias = new ArrayList<String>();
        sentencias.add(UsuarioDB.ElementEntry.CREATE_TABLE);
        sentencias.add(ContactosDB.ElementEntry.CREATE_TABLE);
        sentencias.add(ChatDB.ElementEntry.CREATE_TABLE);
        sentencias.add(MensajeDB.ElementEntry.CREATE_TABLE);
        sentencias.add(MiChatDB.ElementEntry.CREATE_TABLE);

        for (int i=0; i< sentencias.size();i++){
            db.execSQL(sentencias.get(i));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            List<String> sentencias = new ArrayList<String>();
            sentencias.add(UsuarioDB.ElementEntry.DELETE_TABLE);
            sentencias.add(ContactosDB.ElementEntry.DELETE_TABLE);
            sentencias.add(ChatDB.ElementEntry.DELETE_TABLE);
            sentencias.add(MensajeDB.ElementEntry.DELETE_TABLE);
            sentencias.add(MiChatDB.ElementEntry.DELETE_TABLE);

            for (int i = 0; i < sentencias.size(); i++) {
                db.execSQL(sentencias.get(i));
            }
        }
    }
}
