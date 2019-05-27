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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    Object[] datos_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_chat);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String myMacAddress = android.provider.Settings.Secure.getString(getApplication().getContentResolver(), "bluetooth_address");
        Toast.makeText(this, myMacAddress, Toast.LENGTH_SHORT).show();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.BT_NO_DISP , Toast.LENGTH_SHORT).show();
            finish();
        }
        //Toast.makeText(this, bluetoothAdapter.getAddress(), Toast.LENGTH_SHORT).show();
        findByIds();
        configurarActividad();

        // Instancia clase chat adapter enviada a listado
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.bt_element_right_msg);
        listView.setAdapter(chatArrayAdapter);

        mostrarConversacion();

        // Evento click al botón enviar
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!textoMensaje.getText().toString().trim().isEmpty()) {
                    try {
                        sendChatMessage(textoMensaje.getText().toString().trim());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                    mostrarMensaje(datos_msg[4].toString(), true);
                    guardarMensaje(datos_msg);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    try {
                        Object[] datos_recbidos = deserialize(readBuf);
                        String msg_recibido = datos_recbidos[4].toString();
                        mostrarMensaje(msg_recibido, false);
                        guardarMensajeRecibido(datos_recbidos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
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

    private void sendChatMessage(String mensaje) throws IOException {
        if (mensaje.length() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
            String fecha = simpleDateFormat.format(new Date());
            if (chatController.getState() != ChatController.STATE_CONNECTED) { // Si no hay conexión
                Toast.makeText(this, R.string.LOST_CONNECTION, Toast.LENGTH_SHORT).show();
                datos_msg = new Object[] {
                        "null", // id_mensaje
                        direccion_destino, // id_chat
                        fecha,
                        "texto", // tipo_mensaje
                        mensaje, //
                        1, 0, 0,
                        "Me",
                        direccion_destino,
                        1, 0
                };
                guardarMensaje(datos_msg);
                mostrarMensaje(datos_msg[4].toString(), true);
                textoMensaje.setText("");
                return;
            }
            datos_msg = new Object[] {
                    "null", // id_mensaje
                    direccion_destino, // id_chat
                    fecha,
                    "texto", // tipo_mensaje
                    mensaje, //
                    1, 0, 1,
                    "Me",
                    direccion_destino,
                    1, 1
            };
            chatController.write(serialize(datos_msg), "texto");
            textoMensaje.setText("");
        }
    }


    public void configurarActividad(){
        i = getIntent();
        nombre_destino = i.getStringExtra("nombre_destino");
        direccion_destino = i.getStringExtra("direccion_destino");
        nombreDispositivo.setText(nombre_destino + " " + direccion_destino);
    }


    public void guardarMensaje(Object[] msg){ // Enviados y no enviados
        entidad_mensaje = new Mensaje(msg[0].toString(), msg[1].toString(), msg[2].toString(), msg[3].toString(),
                msg[4].toString(), (int) msg[5], (int) msg[6], (int) msg[7], msg[8].toString(), msg[9].toString(),
                (int) msg[10], (int) msg[11] );
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void guardarMensajeRecibido(Object[] msg){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        int estado_envio = 0;
        if ((int) msg[11] == 1){
            msg[1] = connectingDevice.getAddress();
            msg[8] = connectingDevice.getAddress();
        }
        //msg[11] = (int) msg[11] + 1;
        entidad_mensaje = new Mensaje(msg[0].toString(), msg[1].toString(), fecha, msg[3].toString(),
                msg[4].toString(), (int) msg[5], (int) msg[6], (int) msg[7], msg[8].toString(), msg[9].toString(),
                0, (int) msg[11] );
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void mostrarConversacion(){
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
        List<Mensaje> mensajes = MensajeDB.getAllNotSendMessages(getApplicationContext()); // mensajes no enviados
        Iterator<Mensaje> iterator = mensajes.iterator();
        while(iterator.hasNext()){
            Mensaje msg = iterator.next();
            if (chatController.getState() == ChatController.STATE_CONNECTED) {
                if (msg.getContent().length() > 0) {
                    //Aqui va la condicion si salto es mayor a uno y coinciden mac
                    if (msg.getMAC_DESTINO().equals(connectingDevice.getAddress()))
                        Toast.makeText(this, "Es para el dispositivo", Toast.LENGTH_SHORT).show();
                        datos_msg = new Object[] { msg.getID_MESSAGE(), msg.getID_CHAT(), msg.getDate(),
                            msg.getType(), msg.getContent(), msg.getTime(), msg.EstaoLectura(), 1,
                            msg.getMAC_ORIGEN(), msg.getMAC_DESTINO(), 1, (int) msg.getSaltos() + 1  };
                    try{
                        MensajeDB.eliminarDuplicado(getApplicationContext(), msg.getID_MESSAGE());
                        mostrarConversacion();
                        chatController.write(serialize(datos_msg), "texto");
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


    // Métodos para convertir objeto a byte y viceversa

    public static byte[] serialize(Object[] obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object[] deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Object[]) is.readObject();
    }
}
