package com.android.wondercom.Entities;

public class Usuario {
    private String MAC;
    private String User_Name;

    public Usuario(String mac, String nickname) {
        this.MAC = mac;
        this.User_Name = nickname;
    }

    public String getMac(){
        return this.MAC;
    }

    public String getUserName(){
        return this.MAC;
    }
}
