package com.android.wondercom.BLUETOOTH.Fragments;

import android.app.Fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.wondercom.BLUETOOTH.Adapters.AdapterCercanos;
import com.android.wondercom.BLUETOOTH.BluetoothConnect;
import com.android.wondercom.R;

import java.util.Set;


public class FragmentCercanos extends Fragment{

    BluetoothConnect bluetoothConnect = new BluetoothConnect();

    CardView cv;
    Set<BluetoothDevice> Dispositivos;
    String ListadoDispositivos[];
    int cont = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bt_fragment_cercanos, container, false);
        cv = (CardView) view.findViewById(R.id.cv);
        listarDispositivosCercanos();
        adaptarListado(view);
        // Evento a items de lista (para conectarse o abrir conversación)
        /*cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectar();
            }
        });*/
        return view;
    }

    public void listarDispositivosCercanos(){
        Dispositivos = new BluetoothConnect().getListContactBluetooth();
        for (BluetoothDevice device : Dispositivos){
            cont++;
        }
        ListadoDispositivos = new String [ cont ];
        cont = 0;
        for (BluetoothDevice device : Dispositivos){
            ListadoDispositivos[cont] = device.getName().toString() + device.getAddress().toString();
            cont++;
        }
        cont = 0;
    }

    public void adaptarListado(View view){
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv_cercanos);
        AdapterCercanos adapter = new AdapterCercanos(getActivity(), ListadoDispositivos);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }


    public void conectar(){
        Toast.makeText(getActivity().getApplicationContext(), "Funcionando evento", Toast.LENGTH_SHORT).show();
    }

}
