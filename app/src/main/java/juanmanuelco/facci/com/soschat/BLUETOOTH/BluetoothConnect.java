package juanmanuelco.facci.com.soschat.BLUETOOTH;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

public class BluetoothConnect {
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> Dispositivos;
    private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

    public BluetoothConnect() {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    // Comprueba que el dispositivo admite bluetooth
    public boolean comprobarBluetooth(){
        boolean soportaBluetooth = true;
        if ( bluetooth == null) {
            soportaBluetooth = false;
        }
        return soportaBluetooth;
    }

    // Habilita el bluetooth en caso de no estarlo
    public void habilitarBluetooth(){
        if (!bluetooth.isEnabled()){
            bluetooth.enable();
        }
    }

    // Retorna la lista de dispositivos emparejados.
    public Set<BluetoothDevice> getListContactBluetooth (){
        Dispositivos =  this.bluetoothAdapter.getBondedDevices();
        return Dispositivos;
    }
}
