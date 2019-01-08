package juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades;

public class Contacto {
    private Integer ID, ID_User;
    private String MAC, Username;

    public Contacto(Integer ID, Integer ID_User, String MAC, String username) {
        this.ID = ID;
        this.ID_User = ID_User;
        this.MAC = MAC;
        this.Username = username;
    }

    public Contacto(Integer ID_User, String MAC, String username) {
        this.ID_User = ID_User;
        this.MAC = MAC;
        this.Username = username;
    }

    public Integer getID(){return this.ID; }

    public Integer getIDUser(){ return this.ID_User; }

    public String getMAC(){ return this.MAC; }

    public String getUsername(){ return this.Username; }

}
