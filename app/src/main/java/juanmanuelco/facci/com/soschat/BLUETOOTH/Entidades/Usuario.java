package juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades;

public class Usuario {
    private Integer ID;
    private String MAC;
    private String User_Name;

    public Usuario(Integer ID, String mac, String nickname) {
        this.ID = ID;
        this.MAC = mac;
        this.User_Name = nickname;
    }

    public String getMac(){
        return this.MAC;
    }

    public Integer getID(){
        return this.ID;
    }

    public String getUserName(){
        return this.MAC;
    }
}
