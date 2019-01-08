package juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades;

public class Chat {
    private String id;
    private String fecha;
    private Integer estado;

    public Chat(String id, String fecha, int estado) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
    }

    public void setEstado(Integer estado){
        this.estado = estado;
    }

    public void setDate(String date){
        this.fecha = date;
    }

    public String getID(){
        return this.id;
    }

    public String getDate(){
        return this.fecha;
    }

    public Integer getEstado(){
        return this.estado;
    }

}
