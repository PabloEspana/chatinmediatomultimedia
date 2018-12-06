package juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades;

public class Grupo {
    private String Group_ID;
    private String Group_Name;
    private long Group_Date;

    public Grupo(String group_ID, String group_Name, long group_Date) {
        this.Group_ID = group_ID;
        this.Group_Name = group_Name;
        this.Group_Date = group_Date;
    }

    public String getID(){
        return this.Group_ID;
    }
    public String getName(){
        return this.Group_Name;
    }
    public long getDate(){
        return this.Group_Date;
    }
}

