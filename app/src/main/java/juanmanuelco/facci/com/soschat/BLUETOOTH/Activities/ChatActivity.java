package juanmanuelco.facci.com.soschat.BLUETOOTH.Activities;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Controllers.ChatController;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.ChatDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.MensajeDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entities.ChatMessage;
import juanmanuelco.facci.com.soschat.Entidades.Item;
import juanmanuelco.facci.com.soschat.R;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.ChatArrayAdapter;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Mensaje;


public class ChatActivity extends AppCompatActivity {

    private TextView nombreDispositivo, estadoConexion, color;
    private EditText edit;
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText textoMensaje; // texto a enviar
    private Button botonEnviar;  // 
    private boolean lado = false;

    public BluetoothAdapter bluetoothAdapter;  // adaptador bluetooth

    Intent i;
    String nombre_destino;
    String direccion_destino;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    private BluetoothDevice connectingDevice;

    Mensaje entidad_mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_chat);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no disponible!", Toast.LENGTH_SHORT).show();
            finish();
        }
        findByIds();
        configurarActividad();

        // Instancia clase chat adapter enviada a listado
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.bt_element_right_msg);
        listView.setAdapter(chatArrayAdapter);

        mostrarConversacion();

        /*textoMensaje.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });*/

        // Evento click al botón enviar
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage(textoMensaje.getText().toString());
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new ChatController(this, handler);
            connectToDevice(direccion_destino);
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:  // Si es mensaje de cambio de estado
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:  // Si es estado conectado
                            //setStatus("Conectado a: " + connectingDevice.getName());  // Se envía el mensaje como parámetro
                            setStatus(2);
                            break;
                        case ChatController.STATE_CONNECTING:  // Si es estado conectando
                            //setStatus("Conectando...");  // Se envía el mensaje como parámetro
                            setStatus(1);  // Se envía el mensaje como parámetro
                            break;
                        case ChatController.STATE_LISTEN:
                            //setStatus("Escuchando");
                        case ChatController.STATE_NONE:
                            //setStatus("No conectado");
                            setStatus(3);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:    // Si es mensaje escrito
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);  // Se almacena el mensaje a mostrar
                    //Toast.makeText(ChatActivity.this, "Yo: " + writeMessage, Toast.LENGTH_SHORT).show();
                    mostrarMensaje(writeMessage, true);
                    guardarMensajeEnviado(writeMessage, 1);
                    break;
                case MESSAGE_READ:      // Si es mensaje lectura
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //Toast.makeText(ChatActivity.this, connectingDevice.getName() + ":  " + readMessage,
                            //Toast.LENGTH_SHORT).show();
                    mostrarMensaje(readMessage, false);
                    guardarMensajeRecibido(readMessage, 0);
                    break;

                case MESSAGE_DEVICE_OBJECT:     // Si es mensaje del objeto del dispositivo
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);      // Se guarda los datos del dispositivo
                    Toast.makeText(getApplicationContext(), "Conectado a " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });


    private void setStatus(int s) {
        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        String texto = "";
        if (s == 1){
            texto = "Conectando...";
            estadoConexion.setTextColor(Color.YELLOW);
        }else if (s == 2){
            texto = "Conectado";
            estadoConexion.setTextColor(Color.GREEN);
        }else if (s == 3){
            texto = "Sin conexión";
            estadoConexion.setTextColor(Color.RED);
        }
        estadoConexion.setText(texto);
    }



    private void connectToDevice(String MAC) {
        if (bluetoothAdapter == null) {
            Log.i("Error", "Bluetooth not supported");
        } else {
            BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(MAC);
            //Toast.makeText(this, MAC, Toast.LENGTH_SHORT).show();
            chatController.connect(dispositivo);
        }
    }

    public boolean mostrarMensaje(String mensaje, boolean tipo){
        chatArrayAdapter.add(new ChatMessage(tipo, mensaje));
        textoMensaje.setText("");
        //lado = !lado;
        return true;
    }

    private void sendChatMessage(String mensaje){
    //private boolean sendChatMessage(){
        /*chatArrayAdapter.add(new ChatMessage(lado, textoMensaje.getText().toString()));
        textoMensaje.setText("");
        lado = !lado;
        return true;*/
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, "¡Connexión perdida!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mensaje.length() > 0) {
            byte[] send = mensaje.getBytes();
            chatController.write(send, "texto");
            textoMensaje.setText("");
        }
    }

    // Personalizacción de actividad
    public void configurarActividad(){
        i = getIntent();
        nombre_destino = i.getStringExtra("nombre_destino");
        direccion_destino = i.getStringExtra("direccion_destino");
        nombreDispositivo.setText(nombre_destino + " " + direccion_destino);
        // Cambiar foto
        // Obtener  mensajes de BD y listarlos
    }


    public void guardarMensajeEnviado(String msg, Integer esMio){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        String id_chat = nombre_destino + " " + direccion_destino;
        entidad_mensaje = new Mensaje(id_chat, fecha, "texto", msg, 1, 0, 0, direccion_destino, esMio);
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void guardarMensajeRecibido(String msg, Integer esMio){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        String id_chat = nombre_destino + " " + direccion_destino;
        entidad_mensaje = new Mensaje(id_chat, fecha, "texto", msg, 1, 0, 0, "Sin definir", esMio);
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void mostrarConversacion(){
        String id_chat = nombre_destino + " " + direccion_destino;
        List<Mensaje> mensajes = MensajeDB.getAllMessages(getApplicationContext(), id_chat);
        Iterator<Mensaje> iterator = mensajes.iterator();
        while(iterator.hasNext()){
            Mensaje msg = iterator.next();
            if (msg.EsMio() == 1){
                mostrarMensaje(msg.getContent(), true);
            }else{
                mostrarMensaje(msg.getContent(), false);
            }
        }

    }

    public void findByIds(){
        botonEnviar = (Button) findViewById(R.id.btnSend);
        listView = (ListView) findViewById(R.id.listViewMsg);
        textoMensaje = (EditText) findViewById(R.id.txtMsg);
        nombreDispositivo = (TextView) findViewById(R.id.nombre);
        estadoConexion = (TextView) findViewById(R.id.estadoConexion);
        color = (TextView) findViewById(R.id.color);
    }

}
