package juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades;

public class Mensaje {
    private Integer TEMPO, ELECTURA, EENVIO, ESMIO, SALTOS, MOSTRAR;
    private String ID_MESSAGE, ID_CHAT, TIPO, FECHA, MAC_ORIGEN, MAC_DESTINO,  CONTENT;


    public Mensaje(String ID_MESSAGE, String ID_CHAT, String FECHA, String TIPO, String CONTENT,
                   Integer TEMPO, Integer ELECTURA, Integer EENVIO, String MAC_ORIGEN, String MAC_DESTINO, Integer ESMIO,
                   Integer SALTOS, Integer MOSTRAR) {

        this.ID_MESSAGE = ID_MESSAGE;
        this.ID_CHAT = ID_CHAT;
        this.FECHA = FECHA;
        this.TIPO = TIPO;
        this.CONTENT = CONTENT;
        this.TEMPO = TEMPO;
        this.ELECTURA = ELECTURA;
        this.EENVIO = EENVIO;
        this.MAC_ORIGEN = MAC_ORIGEN;
        this.MAC_DESTINO = MAC_DESTINO;
        this.ESMIO = ESMIO;
        this.SALTOS = SALTOS;
        this.MOSTRAR = MOSTRAR;

    }

    public String getID_MESSAGE(){ return this.ID_MESSAGE; }

    public String getID_CHAT(){ return this.ID_CHAT; }

    public String getMAC_ORIGEN(){ return this.MAC_ORIGEN; }

    public String getMAC_DESTINO(){ return this.MAC_DESTINO; }

    public String getDate(){ return this.FECHA; }

    public String getType(){ return this.TIPO; }

    public String getContent(){ return this.CONTENT; }

    public Integer getTime(){ return this.TEMPO; }

    public Integer EstaoLectura(){ return this.ELECTURA; }

    public Integer EstaoEnvio(){ return this.EENVIO; }

    public Integer EsMio(){ return this.ESMIO; }

    public Integer getSaltos(){ return this.SALTOS; }

    public Integer getMostrar(){ return this.MOSTRAR; }
}
