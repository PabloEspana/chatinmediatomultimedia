package juanmanuelco.facci.com.soschat;

import android.app.Activity;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import juanmanuelco.facci.com.soschat.BLUETOOTH.MainActivityBT;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.MainDB;
import android.view.Menu;


import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.NEGOCIO.DireccionMAC;
import juanmanuelco.facci.com.soschat.NEGOCIO.Dispositivo;
import juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes;
import juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones;
import juanmanuelco.facci.com.soschat.Reflexion.ReflectionUtils;

import static juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes.cargando;
import static juanmanuelco.facci.com.soschat.NEGOCIO.Mensajes.mostrarMensaje;
import static juanmanuelco.facci.com.soschat.NEGOCIO.Validaciones.vacio;

public class InicioActivity extends AppCompatActivity {
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    TextView longitudeValueGPS, latitudeValueGPS;
    EditText ET_Main_Nickname;
    ProgressDialog pDialog;
    SharedPreferences sharedPref;
    WifiManager wifiManager;
    static DB_SOSCHAT db;
    static MainDB db_bluetooth;
    private Locale locale;
    private Configuration config = new Configuration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        db = new DB_SOSCHAT(this);
        db_bluetooth = new MainDB(this);
        Dispositivo.requestPermissionFromDevice(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        pDialog = new ProgressDialog(this);
        ET_Main_Nickname = findViewById(R.id.ET_Main_Nickname);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        ET_Main_Nickname.setText(sharedPref.getString("nickname", Dispositivo.getDeviceName()));
        db.finVidaMensaje(System.currentTimeMillis());
        //cambiarIdioma();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        longitudeValueGPS = (TextView) findViewById(R.id.longitudeValueGPS);
        latitudeValueGPS = (TextView) findViewById(R.id.latitudeValueGPS);
    }

    public void bluetooth(View v) {
        abrirChat(0);
    }

    public void wifi(View v) {
        abrirChat(1);
    }

    public void abrirChat(int valor) {
        Mensajes.cargando(R.string.VERIFY, pDialog, this);
        String nickname = ET_Main_Nickname.getText().toString();
        if (Validaciones.vacio(new EditText[]{ET_Main_Nickname})) {
            GuardarPreferencia(nickname);
            Intent act_chat = null;
            if (valor == 0) {
                act_chat = new Intent(InicioActivity.this, MainActivityBT.class);
                //act_chat.putExtra("nickname", nickname );
            } else if (valor == 1) {
                act_chat = new Intent(InicioActivity.this, FuncionActivity.class);
            }
            DireccionMAC.wifiNombre = ET_Main_Nickname.getText().toString();
            startActivity(act_chat);
        } else Mensajes.mostrarMensaje(R.string.ERROR, R.string.NONAME, this);
    }

    public void GuardarPreferencia(String name) {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("nickname", name);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pDialog != null) pDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configuracion, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) pDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sub_es:
                locale = new Locale("es");
                config.locale = locale;
                getResources().updateConfiguration(config, null);
                Intent refresh = new Intent(InicioActivity.this, InicioActivity.class);
                startActivity(refresh);
                finish();
                return true;
            case R.id.sub_us:
                locale = new Locale("en");
                config.locale = locale;
                getResources().updateConfiguration(config, null);
                refresh = new Intent(InicioActivity.this, InicioActivity.class);
                startActivity(refresh);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setLocale(String idioma) {
        Locale myLocale = new Locale(idioma);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //Intent refresh = new Intent(this, InicioActivity.class);
        //startActivity(refresh);
        //finish();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Por favor active su ubicación")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void toogleGPSUpdates(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals(getResources().getString(R.string.PAUSE))) {
            locationManager.removeUpdates(locationListenerGPS);
            button.setText(R.string.RESUME);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
            button.setText(R.string.PAUSE);
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueGPS.setText(longitudeGPS + "");
                    latitudeValueGPS.setText(latitudeGPS + "");
                    Toast.makeText(InicioActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
