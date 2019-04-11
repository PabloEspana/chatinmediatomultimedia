package juanmanuelco.facci.com.soschat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

import juanmanuelco.facci.com.soschat.BLUETOOTH.MainActivityBT;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.MainDB;


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
    EditText ET_Main_Nickname;
    ProgressDialog pDialog;
    SharedPreferences sharedPref;
    WifiManager wifiManager;
    static DB_SOSCHAT db;
    static MainDB db_bluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        db= new DB_SOSCHAT(this);
        db_bluetooth = new MainDB(this);
        Dispositivo.requestPermissionFromDevice(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        pDialog=new ProgressDialog(this);
        ET_Main_Nickname= findViewById(R.id.ET_Main_Nickname);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        ET_Main_Nickname.setText(sharedPref.getString("nickname", Dispositivo.getDeviceName()));
        db.finVidaMensaje(System.currentTimeMillis());
        cambiarIdioma();
    }

    public void bluetooth (View v){
        abrirChat(0);
    }

    public void wifi (View v){
        abrirChat(1);
    }
    public void abrirChat(int valor){
        Mensajes.cargando(R.string.VERIFY, pDialog, this);
        String nickname= ET_Main_Nickname.getText().toString();
        if(Validaciones.vacio(new EditText[]{ET_Main_Nickname})){
            GuardarPreferencia(nickname);
            Intent act_chat= null;
            if(valor == 0) {
                act_chat= new Intent(InicioActivity.this, MainActivityBT.class);
                //act_chat.putExtra("nickname", nickname );
            }
            else if(valor==1) {
                act_chat= new Intent(InicioActivity.this, FuncionActivity.class);
            }
            DireccionMAC.wifiNombre=ET_Main_Nickname.getText().toString();
            startActivity(act_chat);
        }else Mensajes.mostrarMensaje(R.string.ERROR, R.string.NONAME, this);
    }
    public void GuardarPreferencia(String name){
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
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) pDialog.dismiss();
    }
    
    public void cambiarIdioma(){
        Switch idioma = (Switch)findViewById(R.id.idioma);
        idioma.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    setLocale("es");

                }else{
                    setLocale("en");
                }
            }
        });

    }

    public void setLocale(String idioma){
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
}
