package com.android.wondercom.Entities;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Miembro {
    private long member_ID;
    private String member_MAC;
    private String memberName;
    private long member_Date;

    public Miembro(String member_MAC, String memberName) {
        this.member_MAC = member_MAC;
        this.memberName = memberName;
        this.member_Date = new Date().getTime();;
    }

    public Miembro(long member_ID, String member_MAC, String memberName, long member_Date) {
        this.member_ID = member_ID;
        this.member_MAC = member_MAC;
        this.memberName = memberName;
        this.member_Date = member_Date;
    }

    public void setID(long id){
        this.member_ID = id;
    }

    public void setName(String name){
        this.memberName =  name;
    }

    public void setMAC(String MAC){
        this.member_MAC =  MAC;
    }

    public void setDate(long date){
        this.member_Date =  date;
    }

    public long getID(){
        return this.member_ID;
    }

    public String getName(){
        return this.memberName;
    }

    public String getMAC(){
        return this.member_MAC;
    }

    public long getDate(){
        return this.member_Date;
    }

    public String getDateFormat(){
        Date date = new Date(this.member_Date);
        Format format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        return format.format(date);
    }
}
