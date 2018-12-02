package juanmanuelco.facci.com.soschat.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MainDB /*extends SQLiteOpenHelper*/ {
    /*
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SOSChat.db";

    public MainDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> sentencias = new ArrayList<String>();
        sentencias.add(UsuarioDB.ElementEntry.CREATE_TABLE);
        sentencias.add(GrupoDB.ElementEntry.CREATE_TABLE);
        sentencias.add(MiembroDB.ElementEntry.CREATE_TABLE);

        for (int i=0; i< sentencias.size();i++){
            db.execSQL(sentencias.get(i));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<String> sentencias = new ArrayList<String>();
        sentencias.add(UsuarioDB.ElementEntry.DELETE_TABLE);
        sentencias.add(GrupoDB.ElementEntry.DELETE_TABLE);
        sentencias.add(MiembroDB.ElementEntry.DELETE_TABLE);

        for (int i=0; i< sentencias.size();i++){
            db.execSQL(sentencias.get(i));
        }
    }*/
}
