package com.android.wondercom.Entities;

public class Miembro {
    private String member_ID;
    private String member_MAC;
    private String memberName;
    private long member_Date;

    public Miembro(String member_ID, String member_MAC, String memberName, long member_Date) {
        this.member_ID = member_ID;
        this.member_MAC = member_MAC;
        this.memberName = memberName;
        this.member_Date = member_Date;
    }
}
