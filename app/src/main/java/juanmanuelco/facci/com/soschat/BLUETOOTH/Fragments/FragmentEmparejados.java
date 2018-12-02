package juanmanuelco.facci.com.soschat.BLUETOOTH.Fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Set;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.EmparejadosAdapter;
import juanmanuelco.facci.com.soschat.BLUETOOTH.BluetoothConnect;
import juanmanuelco.facci.com.soschat.R;

public class FragmentEmparejados extends Fragment {

    BluetoothConnect bluetoothConnect = new BluetoothConnect();

    CardView cv;
    Set<BluetoothDevice> Dispositivos;
    String ListadoDispositivos[];
    int cont = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bt_fragment_emparejados, container, false);
        cv = (CardView) view.findViewById(R.id.cv);
        listarDispositivosCercanos();
        adaptarListado(view);
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
        EmparejadosAdapter adapter = new EmparejadosAdapter(getActivity(), ListadoDispositivos);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
    }

    public void conectar(){
        Toast.makeText(getActivity().getApplicationContext(), "Funcionando evento", Toast.LENGTH_SHORT).show();
    }
}
