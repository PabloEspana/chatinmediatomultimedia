package juanmanuelco.facci.com.soschat.BLUETOOTH.Fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    Set<BluetoothDevice> Dispositivos; // set de dispositivos bluetooth
    String ListadoDispositivos[];
    int cont = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.bt_fragment_emparejados, container, false);
        cv = (CardView) vista.findViewById(R.id.cv);
        listarDispositivosCercanos();
        adaptarListado(vista);
        setHasOptionsMenu(true);
        return vista;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.bt_menu_fragment1, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reload:
                Toast.makeText(getActivity(), "Refrescando", Toast.LENGTH_SHORT).show();
                listarDispositivosCercanos();
                adaptarListado(getView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
