package juanmanuelco.facci.com.soschat.BLUETOOTH;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.text.SimpleDateFormat;
import java.util.Date;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.SectionsPageAdapter;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.ChatDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Fragments.FragmentChats;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Fragments.FragmentEmparejados;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Fragments.FragmentGrupos;
import juanmanuelco.facci.com.soschat.R;


public class MainActivityBT extends AppCompatActivity {

    BluetoothConnect bluetoothConnect = new BluetoothConnect();
    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_main);

        bluetoothConnect.comprobarBluetooth();
        bluetoothConnect.habilitarBluetooth();
        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        configurarViewPager(viewPager);

        // Agregando soporte action bar a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configuracion de pestañas
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Chat comunidad = new Chat(getString(R.string.SOSCHAT_COMUNITY), simpleDateFormat.format(new Date()),1);
        ChatDB.Insert(this, comunidad);
    }

    // Este método configura el menú de la aplicación
    private void configurarViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentEmparejados(), getString(R.string.DEVICES_BT));
        adapter.addFragment(new FragmentChats(), getString(R.string.CHATS_BT));
        adapter.addFragment(new FragmentGrupos(), getString(R.string.GROUPS_BT));
        viewPager.setAdapter(adapter);
    }

}
