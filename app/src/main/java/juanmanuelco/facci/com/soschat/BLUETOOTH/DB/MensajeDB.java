package juanmanuelco.facci.com.soschat.BLUETOOTH.DB;

import android.content.Context;
import android.provider.BaseColumns;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;

public class MensajeDB {

    private MainDB database;
    public static final String TABLE_NAME = "MensajeI";
    public static final String ID = "ID";
    public static final String ID_CHAT = "ID_CHAT";
    public static final String FECHA = "FECHA";
    public static final String TIPO = "TIPO";
    public static final String CONTENT = "CONTENT";
    public static final String TEMPO = "CONTENT";
    public static final String ELECTURA = "ESTADO_LECTURA";
    public static final String EENVIO = "ESTADO_ENVIO";
    public static final String MAC = "MAC";

    public MensajeDB(Context context) {
        this.database = new MainDB(context);
    }

    public static abstract class ElementEntry implements BaseColumns {
        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " ("+ID+" int primary key autoincrement, "+ID_CHAT+" text not null, "+FECHA+" text not null, " +
                TIPO+" int not null, "+TEMPO+" int not null, "+CONTENT+" blob not null, "+ELECTURA+" int not null, " +
                EENVIO+" int not null, "+MAC+" text not null, "+
                "foreign key("+ID_CHAT+") references "+ChatDB.TABLE_NAME +" ("+ChatDB.ID+");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
