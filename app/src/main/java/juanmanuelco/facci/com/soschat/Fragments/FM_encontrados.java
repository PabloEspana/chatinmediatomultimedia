/*while (verificar.moveToNext()){
                if (verificar.getPosition()<0){
                    Log.i("Posicion",String.valueOf(verificar.getPosition()) +" No hay nada");
                }else{
                    if (verificar.getString(0)!=address){
                        Log.i("VerDatos","Addres " +verificar.getString(0)+" Nombre " +verificar.getString(1));

                    }
                }
            }
            if (verificar.getCount()<0){
                Log.i("Entro"," al if");
                db.execSQL(String.format("INSERT INTO %s VALUES ( '%s', '%s')",
                        TABLA_ENCONTRADO,address,nickname));
            }else{
                Log.i("Entro"," al else");
                while (verificar.moveToNext()){
                    if (verificar.getString(0)!=address){
                        Log.i("VerDatos","Addres " +verificar.getString(0)+" Nombre " +verificar.getString(1));
                        //db.execSQL(String.format("INSERT INTO %s VALUES ( '%s', '%s')",
                          //      TABLA_ENCONTRADO,address,nickname));
                    }
                }
            }*/
package juanmanuelco.facci.com.soschat.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import juanmanuelco.facci.com.soschat.CustomAdapters.AdaptadorDispositivos;
import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.Entities.ENCONTRADO;
import juanmanuelco.facci.com.soschat.FuncionActivity;
import juanmanuelco.facci.com.soschat.InitThreads.ServerInit;
import juanmanuelco.facci.com.soschat.NEGOCIO.Dispositivo;
import juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones;
import juanmanuelco.facci.com.soschat.R;
import juanmanuelco.facci.com.soschat.Receivers.WifiDirectBroadcastReceiver;


public class FM_encontrados extends Fragment {
    WifiManager wifiManager;
    public static final String TAG = "MainActivity";
    public static final String DEFAULT_CHAT_NAME = Dispositivo.getDeviceName();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private Button goToChat;
    private ImageView goToSettings;
    private TextView goToSettingsText;
    private TextView setChatNameLabel;
    private EditText setChatName;
    private ImageView disconnect;
    public static String chatName;
    public static ServerInit server;
    public static final int request_code = 1000;
    RecyclerView rv_participants;
    int intentos=0;
    ProgressDialog pDialog;

    private SearchView searchView = null;
    private boolean searchViewShow = false;
    private SearchView.OnQueryTextListener queryTextListener;
    public ArrayList<String> listado2 = new ArrayList<>();
    public ArrayList<String[]> nueva = new ArrayList<>();
    private ArrayList<ENCONTRADO> ListaFound;
    private DB_SOSCHAT db;
    private ENCONTRADO encontrados;
    private int ban=0;
    private String[] caracter;
    private AdaptadorDispositivos adapter;

    //Getters and Setters
    public WifiP2pManager getmManager() { return mManager; }
    public WifiP2pManager.Channel getmChannel() { return mChannel; }
    public WifiDirectBroadcastReceiver getmReceiver() { return mReceiver; }
    public IntentFilter getmIntentFilter() { return mIntentFilter; }
    public Button getGoToChat(){ return goToChat; }
    public TextView getSetChatNameLabel() { return setChatNameLabel; }
    public ImageView getGoToSettings() { return goToSettings; }
    public EditText getSetChatName() { return setChatName; }
    public TextView getGoToSettingsText() { return goToSettingsText; }
    public ImageView getDisconnect() { return disconnect; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_encontrados, container, false);
        setHasOptionsMenu(true);
        db = new DB_SOSCHAT(getContext());
        encontrados = new ENCONTRADO();
        pDialog=new ProgressDialog(getActivity());
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        requestPermissionFromDevice();
        rv_participants=v.findViewById(R.id.participants_rv);
        rv_participants.setLayoutManager(new LinearLayoutManager(getActivity()));

        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity().getApplicationContext(), getActivity().getMainLooper(), null);
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmManager(mManager);
        mReceiver.setmChannel(mChannel);
        mReceiver.setmActivity(getActivity());
        mReceiver.setFragment(this);
        mReceiver.setRecycler(rv_participants);
        mReceiver.setDialogo(pDialog);

        FuncionActivity.server=server;
        FuncionActivity.chatName=Validaciones.loadChatName(getActivity(), "nickname", DEFAULT_CHAT_NAME);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        descubrir();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText != null && !newText.isEmpty()) {
                        for (String c: mReceiver.retornar()){
                            caracter=c.split(",");
                            nueva.add(new String[]{caracter[0],caracter[1]});
                        }
                        adapter = new AdaptadorDispositivos(nueva);
                        adapter.getFilter().filter(newText);
                        rv_participants.setAdapter(adapter);
                        //rv_participants.invalidate();
                    }else{
                        if (mReceiver.retornar()!=null && !mReceiver.retornar().isEmpty()){
                            adapter = new AdaptadorDispositivos(mReceiver.listado);
                            rv_participants.setAdapter(adapter);
                            rv_participants.invalidate();
                            //prueba();
                        }
                    }
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void descubrir(){
        try {
            Thread.sleep(300);
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(), "Buscando...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {

                }
            });
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), "No se ha podido iniciar la búsqueda", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
        descubrir();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void requestPermissionFromDevice() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE
        },request_code);
    }
}