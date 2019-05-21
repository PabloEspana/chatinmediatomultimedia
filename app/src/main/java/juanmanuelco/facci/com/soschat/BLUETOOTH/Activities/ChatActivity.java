package juanmanuelco.facci.com.soschat.BLUETOOTH.Activities;

import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Controllers.ChatController;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.MensajeDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entities.ChatMessage;
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
    public static final int MESSAGE_NOT_SEND = 6;
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
            Toast.makeText(this, R.string.BT_NO_DISP , Toast.LENGTH_SHORT).show();
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
                if (!textoMensaje.getText().toString().trim().isEmpty())
                sendChatMessage(textoMensaje.getText().toString().trim());
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
                        case ChatController.STATE_CONNECTED:  // Si es estado conectado  //connectingDevice.getName());
                            setStatus(2);
                            reintentarEnviarMensajes();
                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus(1);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus(3);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String msg_enviado = new String(writeBuf);
                    mostrarMensaje(msg_enviado.substring(0, msg_enviado.length() - 17), true);
                    guardarMensajeEnviado(msg_enviado.substring(0, msg_enviado.length() - 17));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String msg_recibido = new String(readBuf, 0, msg.arg1);
                    mostrarMensaje(msg_recibido.substring(0, msg_recibido.length() - 17), false);
                    guardarMensajeRecibido(
                            msg_recibido.substring(0, msg_recibido.length() - 17),
                            connectingDevice.getAddress(),
                            msg_recibido.substring(msg_recibido.length()-17, msg_recibido.length()));
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), R.string.CONNECTED_TO + connectingDevice.getName(),
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
        String texto = "";
        if (s == 1){
            texto = getString(R.string.CONNECTING);
            estadoConexion.setTextColor(Color.YELLOW);
        }else if (s == 2){
            texto = getString(R.string.CONNECTED);
            estadoConexion.setTextColor(Color.GREEN);
        }else if (s == 3){
            texto = getString(R.string.WITHOUT_CONNECTION);
            estadoConexion.setTextColor(Color.RED);
        }
        estadoConexion.setText(texto);
    }



    private void connectToDevice(String MAC) {
        if (bluetoothAdapter == null) {
            Log.i(getString(R.string.ERROR), getString(R.string.BT_NOT_SUPPORT));
        } else {
            BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(MAC);
            chatController.connect(dispositivo);
        }
    }

    public boolean mostrarMensaje(String mensaje, boolean tipo){
        chatArrayAdapter.add(new ChatMessage(tipo, mensaje));
        textoMensaje.setText("");
        return true;
    }

    private void sendChatMessage(String mensaje){
        /*if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, R.string.LOST_CONNECTION, Toast.LENGTH_SHORT).show();
            guardarMensajeNoEnviado(textoMensaje.getText().toString());
            mostrarMensaje(textoMensaje.getText().toString(), true);
            return;
        }*/
        if (mensaje.length() > 0) {
            mensaje += direccion_destino;
            byte[] send = mensaje.getBytes();
            chatController.write(send, "texto");
            textoMensaje.setText("");
        }
    }

    public void configurarActividad(){
        i = getIntent();
        nombre_destino = i.getStringExtra("nombre_destino");
        direccion_destino = i.getStringExtra("direccion_destino");
        nombreDispositivo.setText(nombre_destino + " " + direccion_destino);
    }


    public void guardarMensajeEnviado(String msg){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        //String id_chat = nombre_destino + " " + direccion_destino;
        entidad_mensaje = new Mensaje("null", direccion_destino, fecha, "texto", msg, 1, 0, 1,
                "Me", direccion_destino, 1);
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void guardarMensajeNoEnviado(String msg){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        //String id_chat = nombre_destino + " " + direccion_destino;
        entidad_mensaje = new Mensaje("null", direccion_destino, fecha, "texto", msg, 1, 0, 0,
                "null", direccion_destino, 1);
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void guardarMensajeRecibido(String msg, String origen, String destino){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        //String id_chat = nombre_destino + " " + direccion_destino;
        entidad_mensaje = new Mensaje("null", origen, fecha, "texto", msg, 1, 0, 1,
                origen, destino, 0);
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void mostrarConversacion(){
        //String id_chat = nombre_destino + " " + direccion_destino;
        String id_chat = direccion_destino;
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

    public void reintentarEnviarMensajes(){
        String id_chat = nombre_destino + " " + direccion_destino;
        List<Mensaje> mensajes = MensajeDB.getAllNotSendMessages(getApplicationContext(), id_chat);
        Iterator<Mensaje> iterator = mensajes.iterator();
        while(iterator.hasNext()){
            Mensaje msg = iterator.next();
            if (chatController.getState() == ChatController.STATE_CONNECTED) {
                if (msg.getContent().length() > 0) {
                    byte[] send = msg.getContent().getBytes();
                    try{
                        MensajeDB.eliminarDuplicado(getApplicationContext(), msg.getID_MESSAGE());
                        mostrarConversacion();
                        chatController.write(send, "texto");
                    }catch (Exception ex) {
                        Log.e("Error al eliminar", ex.toString());
                    }
                }
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
