package juanmanuelco.facci.com.soschat.BLUETOOTH.Entities;

public class ChatMessage {
    public boolean left;
    public String message, tipo_contenido;

    public ChatMessage(boolean left, String message, String tipo_contenido) {
        super();
        this.left = left;
        this.message = message;
        this.tipo_contenido = tipo_contenido;
    }
}
