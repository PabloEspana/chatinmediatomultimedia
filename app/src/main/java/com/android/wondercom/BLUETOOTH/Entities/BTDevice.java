package com.android.wondercom.BLUETOOTH.Entities;

import android.bluetooth.BluetoothDevice;

public class BTDevice {

    private BluetoothDevice Dispositivo;
    private String deviceName;
    private String address;
    private boolean connected;
    private int foto;

    public BTDevice(BluetoothDevice dispositivo, boolean connect, int foto) {
        this.Dispositivo = dispositivo;
        this.deviceName = dispositivo.getName();
        this.address = dispositivo.getAddress();
        this.connected = connect;
        this.foto = foto;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public boolean getConnected() {
        return connected;
    }

    public String getAddress() {
        return address;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public BluetoothDevice getDevice(){
        return Dispositivo;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

}
