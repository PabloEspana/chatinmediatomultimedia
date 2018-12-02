package juanmanuelco.facci.com.soschat.DB;

import android.content.Context;
import android.provider.BaseColumns;

public class MensajeDB {

    /*private MainDB database;
    public static final String TABLE_NAME = "Grupos";

    public MensajeDB(Context context) {
        this.database = new MainDB(context);
    }

    public static abstract class ElementEntry implements BaseColumns {

        public static final String CREATE_TABLE = "Create table if not exists "+TABLE_NAME+
                " (Msg_ID int primary key autoincrement, Msg_MAC_Dest text not null, Msg_MAC_Orig text not null, " +
                "Group_ID text not null, Msg_Type text not null, Msg_Content blob not null, Msg_Date timestamp not null, " +
                "foreign key(Msg_MAC_Orig) references "+MiembroDB.TABLE_NAME +" (Member_MAC)," +
                "foreign key(Msg_MAC_Dest) references "+MiembroDB.TABLE_NAME +" (Member_MAC)," +
                "foreign key(Group_ID) references "+GrupoDB.TABLE_NAME +" (Group_ID) ;";
                //En esta tabla ocurrirá un error al intentar enviar un mensaje, el usuario debe replicarse como miembro o
                // crear una sola tabla de usuarios y esa relacionarla.

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }*/
}
