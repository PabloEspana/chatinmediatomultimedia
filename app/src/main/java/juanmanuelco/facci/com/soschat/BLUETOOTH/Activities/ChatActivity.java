package juanmanuelco.facci.com.soschat.BLUETOOTH.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.concurrent.ExecutionException;

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
    private EditText textoMensaje;
    private Button botonEnviar;
    private boolean lado = false;
    private Toolbar toolbar;

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

    int num_con;

    private static final int SELECT_PICTURE = 43; // código de resultado al escoger imagen
    String tipo_mensaje = "texto";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.BT_NO_DISP , Toast.LENGTH_SHORT).show();
            finish();
        }

        findByIds();
        personalizarActividad();

        // Instancia clase chat adapter enviada a listado
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.bt_element_right_msg);
        listView.setAdapter(chatArrayAdapter);

        mostrarConversacion();

        num_con = 0;

        // Inicio de proceso de envìo de mensaje, pprimer mètodo a ejecutarse
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!textoMensaje.getText().toString().trim().isEmpty()) {
                    try {
                        tipo_mensaje = "texto";
                        enviarMensaje(textoMensaje.getText().toString().trim());
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
            coonectarDispositivo(direccion_destino);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:  // Si es mensaje de cambio de estado
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:  // Si es estado conectado  //connectingDevice.getName());
                            cambiarEstado(2);
                            //num_con++;
                            //if (num_con == 1){
                                reintentarEnviarMensajes();
                            //}
                            break;
                        case ChatController.STATE_CONNECTING:
                            cambiarEstado(1);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            cambiarEstado(3);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    break;
                case MESSAGE_READ:
                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss.SSS");
                    String tiempo2 = simpleDateFormat2.format(new Date());

                    byte[] readBuf = (byte[]) msg.obj;
                    try {
                        Object[] datos_recbidos = deserialize(readBuf); // se deserializa el objeto recibido
                        String msg_recibido = datos_recbidos[4].toString();
                        if((int) datos_recbidos[12]==1){ // // Si se debe mostrar
                            mostrarMensaje(msg_recibido, false, datos_recbidos[3].toString());
                            //mostrarMensaje(msg_recibido + " \n " +tiempo2, false, datos_recbidos[3].toString());
                        }
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

    private void cambiarEstado(int s) {
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

    private void coonectarDispositivo(String MAC) {
        if (bluetoothAdapter == null) {
            Log.i(getString(R.string.ERROR), getString(R.string.BT_NOT_SUPPORT));
        } else {
            BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(MAC);
            chatController.connect(dispositivo);
        }
    }

    public boolean mostrarMensaje(String mensaje, boolean tipo, String tipo_mensaje){
        chatArrayAdapter.add(new ChatMessage(tipo, mensaje, tipo_mensaje));
        textoMensaje.setText("");
        return true;
    }

    private void enviarMensaje(String mensaje) throws IOException {
        if (mensaje.length() > 0) { // Siempre y cuando no estè vacio
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
            String fecha = simpleDateFormat.format(new Date());
            if (chatController.getState() != ChatController.STATE_CONNECTED) { // Si no hay conexión
                Toast.makeText(this, R.string.LOST_CONNECTION, Toast.LENGTH_SHORT).show();
                datos_msg = new Object[] {
                        "null",             // ID_MENSAJE
                        direccion_destino,  // ID_CHAT
                        fecha,              // FECHA
                        tipo_mensaje,       // TIPO
                        mensaje,            // CONTENT
                        1,                  // TEMPO
                        0,                  // ESTADO_LECTURA
                        0,                  // ESTADO_ENVIO
                        "Me",               // MAC_ORIGEN  (no se puede obtener)
                        direccion_destino,  // MAC_DESTINO
                        1,                  // ESMIO
                        0,                  // SALTOS
                        1                   // MOSTRAR
                };

                // Si no hay conexiòn se muestra y se almacena pero no se envìa
                mostrarMensaje(datos_msg[4].toString(), true, datos_msg[3].toString());
                almacenarMensaje(datos_msg);
                textoMensaje.setText("");
                return;
            }

            // Si hay conexxiòn cambian ciertos paràmetros
            datos_msg = new Object[] {
                    "null",             // ID_MENSAJE
                    direccion_destino,  // ID_CHAT
                    fecha,              // FECHA
                    tipo_mensaje,       // TIPO
                    mensaje,            // CONTENT
                    1,                  // TEMPO
                    0,                  // ESTADO_LECTURA
                    1,                  // ESTADO_ENVIO
                    "Me",               // MAC_ORIGEN  (no se puede obtener)
                    direccion_destino,  // MAC_DESTINO
                    1,                  // ESMIO
                    1,                  // SALTOS
                    1                   // MOSTRAR
            };

            // Si hay conexiòn se muestra, se almacena y se envìa
            mostrarMensaje(datos_msg[4].toString(), true, datos_msg[3].toString());
            almacenarMensaje(datos_msg);
            try{
                chatController.write(serialize(datos_msg), tipo_mensaje); // Se ejecuta internamente y muestra el msg
            }catch (Exception e){
                Log.i("Error de envío", e.toString());
            }
            textoMensaje.setText("");
        }
    }

    public void almacenarMensaje(Object[] msg){ // Enviados y no enviados
        entidad_mensaje = new Mensaje(msg[0].toString(), msg[1].toString(), msg[2].toString(), msg[3].toString(),
                msg[4].toString(), (int) msg[5], (int) msg[6], (int) msg[7], msg[8].toString(), msg[9].toString(),
                (int) msg[10], (int) msg[11], (int) msg[12] );
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void guardarMensajeRecibido(Object[] msg){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        // Solo si es el primer salto o punto a punto se obtiene:
        if ((int) msg[11] == 1){
            msg[1] = connectingDevice.getAddress(); // Id del chat
            msg[8] = connectingDevice.getAddress(); // Direcciòn destino
        }
        entidad_mensaje = new Mensaje(msg[0].toString(), msg[1].toString(), fecha, msg[3].toString(),
                msg[4].toString(), (int) msg[5], (int) msg[6], (int) msg[7], msg[8].toString(), msg[9].toString(),
                0, (int) msg[11], (int) msg[12] );
        MensajeDB.Insert(getApplicationContext(), entidad_mensaje);
    }

    public void mostrarConversacion(){
        String id_chat = direccion_destino;
        List<Mensaje> mensajes = MensajeDB.getAllMessages(getApplicationContext(), id_chat);
        Iterator<Mensaje> iterator = mensajes.iterator();
        while(iterator.hasNext()){
            Mensaje msg = iterator.next();
            if (msg.getMostrar() == 1){
                if (msg.EsMio() == 1){
                    mostrarMensaje(msg.getContent(), true, msg.getType());
                }else{
                    mostrarMensaje(msg.getContent(), false, msg.getType());
                }
            }
        }
    }

    public void reintentarEnviarMensajes(){
        List<Mensaje> mensajes = MensajeDB.getAllNotSendMessages(getApplicationContext()); // mensajes no enviados
        Iterator<Mensaje> iterator = mensajes.iterator();
        while(iterator.hasNext()){
            Mensaje msg = iterator.next();
            if (chatController.getState() == ChatController.STATE_CONNECTED) { // siempre y cuando haya conexiòn con algùn dispositivo
                if (msg.getContent().length() > 0) {
                    // Condicion si coinciden mac
                    if (msg.getMAC_DESTINO().equals(connectingDevice.getAddress())){
                        datos_msg = new Object[] {
                                msg.getID_MESSAGE(),        // ID_MENSAJE
                                msg.getID_CHAT(),           // ID_CHAT
                                msg.getDate(),              // FECHA
                                msg.getType(),              // TIPO
                                msg.getContent(),           // CONTENT
                                msg.getTime(),              // TEMPO
                                msg.EstaoLectura(),         // ESTADO_LECTURA
                                1,                          // ESTADO_ENVIO
                                msg.getMAC_ORIGEN(),        // MAC_ORIGEN
                                msg.getMAC_DESTINO(),       // MAC_DESTINO
                                0,                          // ESMIO
                                (int) msg.getSaltos() + 1,  // SALTOS
                                1                           // MOSTRAR
                        };
                    }else{
                        datos_msg = new Object[] {
                                msg.getID_MESSAGE(),        // ID_MENSAJE
                                msg.getID_CHAT(),           // ID_CHAT
                                msg.getDate(),              // FECHA
                                msg.getType(),              // TIPO
                                msg.getContent(),           // CONTENT
                                msg.getTime(),              // TEMPO
                                msg.EstaoLectura(),         // ESTADO_LECTURA
                                0,                          // ESTADO_ENVIO (para que se vuelva a reenviar)
                                msg.getMAC_ORIGEN(),        // MAC_ORIGEN
                                msg.getMAC_DESTINO(),       // MAC_DESTINO
                                0,                          // ESMIO
                                (int) msg.getSaltos() + 1,  // SALTOS
                                0                           // MOSTRAR
                        };
                    }
                    try{
                        /*if ((int) datos_msg[12] == 1){ // si se muestra
                            mostrarMensaje(datos_msg[4].toString(), true, datos_msg[3].toString());
                        }*/
                        chatController.write(serialize(datos_msg), msg.getType());
                    }catch (Exception ex) {
                        Log.e("Ha ocurrido un error", ex.toString());
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

    public void personalizarActividad(){
        i = getIntent();
        nombre_destino = i.getStringExtra("nombre_destino");
        direccion_destino = i.getStringExtra("direccion_destino");
        nombreDispositivo.setText(nombre_destino + " " + direccion_destino);
    }

    public void notificarMensaje(Object[] datos_msg){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("nombre_destino", "");
        intent.putExtra("direccion_destino", datos_msg[8].toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setContentTitle("Nuevo Mensaje");
        mBuilder.setContentText(datos_msg[4].toString());
        mBuilder.setVibrate(new long[] {100, 250, 100, 500});
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_menu_activity_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.reconnect:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                } else {
                    chatController = new ChatController(this, handler);
                    coonectarDispositivo(direccion_destino);
                }
                return true;
            case R.id.escoger_imagen:
                escogerImagen();
                return true;
            case R.id.text_1KB:
                    textoMensaje.setText("");
                return true;
            case R.id.text_32KB:
                textoMensaje.setText("");
                return true;
            case R.id.text_64KB:
                textoMensaje.setText("");
                return true;
            case R.id.text_120KB:
                textoMensaje.setText("");
                return true;
            case R.id.text_256KB:
                textoMensaje.setText("");
                return true;
            case R.id.text_512KB:
                textoMensaje.setText("");
                return true;
            case R.id.text_1MB:
                textoMensaje.setText("");
                return true;
            case R.id.text_2MB:
                textoMensaje.setText("");
                return true;
            case R.id.text_5MB:
                textoMensaje.setText("");
                return true;
            case R.id.text_10MB:
                textoMensaje.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void escogerImagen(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), SELECT_PICTURE);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), SELECT_PICTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK){
                    if(data != null){
                        tipo_mensaje = "imagen";
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                //bitmap = Bitmap.createScaledBitmap(bitmap,  100 ,100, true);
                            byte[] imageBytes = baos.toByteArray();
                            String imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                            enviarMensaje(imageEncoded);

                            baos.close();
                        }catch (Exception e){
                            Toast.makeText(this, "Error de imagen  "+ e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(this, "Error al escoger imagen", Toast.LENGTH_SHORT).show();
                }
        }
    }
}