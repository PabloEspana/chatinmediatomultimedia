package juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades;

import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.ContactosDB;

public class Mensaje {
    private Integer TEMPO, ELECTURA, EENVIO, ESMIO;
    private String ID_MESSAGE, ID_CHAT, TIPO, FECHA, MAC, CONTENT;


    public Mensaje(String ID_MESSAGE, String ID_CHAT, String FECHA, String TIPO, String CONTENT, Integer TEMPO, Integer ELECTURA, Integer EENVIO, String MAC, Integer ESMIO) {
        this.ID_MESSAGE = ID_MESSAGE;
        this.ID_CHAT = ID_CHAT;
        this.FECHA = FECHA;
        this.TIPO = TIPO;
        this.CONTENT = CONTENT;
        this.TEMPO = TEMPO;
        this.ELECTURA = ELECTURA;
        this.EENVIO = EENVIO;
        this.MAC = MAC;
        this.ESMIO = ESMIO;
    }

    public String getID_MESSAGE(){ return this.ID_MESSAGE; }

    public String getID_CHAT(){ return this.ID_CHAT; }

    public String getMAC(){ return this.MAC; }

    public String getDate(){ return this.FECHA; }

    public String getType(){ return this.TIPO; }

    public String getContent(){ return this.CONTENT; }

    public Integer getTime(){ return this.TEMPO; }

    public Integer EstaoLectura(){ return this.ELECTURA; }

    public Integer EstaoEnvio(){ return this.EENVIO; }

    public Integer EsMio(){ return this.ESMIO; }

}
