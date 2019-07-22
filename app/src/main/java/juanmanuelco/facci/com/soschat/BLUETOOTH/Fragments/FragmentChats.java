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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.ChatsAdapter;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.EmparejadosAdapter;
import juanmanuelco.facci.com.soschat.BLUETOOTH.BluetoothConnect;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.ChatDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.MensajeDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Mensaje;
import juanmanuelco.facci.com.soschat.R;

public class FragmentChats extends Fragment{

    CardView cv;
    String ListadoChats[];
    ChatDB chatDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.bt_fragment_mensajes, container, false);
        cv = (CardView) vista.findViewById(R.id.cv2);
        chatDB = new ChatDB(getContext());
        listarChats();
        adaptarListado(vista);
        setHasOptionsMenu(true);
        return vista;
    }

    public void listarChats(){
        List<Chat> conversaciones = ChatDB.obtenerConversacionesActivas(getContext());
        Iterator<Chat> iterator = conversaciones.iterator();
        ListadoChats = new String [ conversaciones.size() ];
        int cont = 0;
        while (iterator.hasNext()) {
            Chat chat = iterator.next();
            //if (! chat.getID().equals("Comunidad SosChat")){
                ListadoChats[cont] = chat.getID();
                cont ++;
            //}
        }
        cont = 0;
    }

    public void adaptarListado(View view){
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv_chats);
        ChatsAdapter adapter = new ChatsAdapter(getActivity(), ListadoChats);
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
                listarChats();
                adaptarListado(getView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
