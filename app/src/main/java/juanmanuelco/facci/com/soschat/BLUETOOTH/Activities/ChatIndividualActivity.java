package juanmanuelco.facci.com.soschat.BLUETOOTH.Activities;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Services.BluetoothService;
import juanmanuelco.facci.com.soschat.BLUETOOTH.DB.MensajeDB;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entities.ChatMessage;
import juanmanuelco.facci.com.soschat.R;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.MsgArrayAdapter;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Mensaje;

import static juanmanuelco.facci.com.soschat.BLUETOOTH.Services.BluetoothService.*;

public class ChatIndividualActivity extends AppCompatActivity {

    private TextView nombreDispositivo, estadoConexion, color;
    private EditText edit;
    private MsgArrayAdapter msgArrayAdapter;
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
    private BluetoothService bluetoothService;
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
            Toast.makeText(this, R.string.BT_NO_DISP, Toast.LENGTH_SHORT).show();
            finish();
        }

        findByIds();
        personalizarActividad();

        // Instancia clase chat adapter enviada a listado
        msgArrayAdapter = new MsgArrayAdapter(getApplicationContext(), R.layout.bt_element_right_msg);
        listView.setAdapter(msgArrayAdapter);

        mostrarConversacion();

        num_con = 0;

        // Inicio de proceso de envìo de mensaje, pprimer mètodo a ejecutarse
        botonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!textoMensaje.getText().toString().trim().isEmpty()) {
                    try {
                        tipo_mensaje = "texto";
                        prepararObjMensaje(textoMensaje.getText().toString().trim());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(msgArrayAdapter);

        //to scroll the list view to bottom on data change
        msgArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(msgArrayAdapter.getCount() - 1);
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
            bluetoothService = new BluetoothService(this, handler);
            coonectarDispositivo(direccion_destino);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:  // Si es mensaje de cambio de estado
                    switch (msg.arg1) {
                        case STATE_CONNECTED:  // Si es estado conectado  //connectingDevice.getName());
                            cambiarEstado(2);
                            //reintentarEnviarMensajes();
                            break;
                        case STATE_CONNECTING:
                            cambiarEstado(1);
                            break;
                        case STATE_LISTEN:
                        case STATE_NONE:
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
                        if ((int) datos_recbidos[12] == 1) { // // Si se debe mostrar
                            mostrarMensaje(msg_recibido, false, datos_recbidos[3].toString());
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
        if (s == 1) {
            texto = getString(R.string.CONNECTING);
            estadoConexion.setTextColor(Color.YELLOW);
        } else if (s == 2) {
            texto = getString(R.string.CONNECTED);
            estadoConexion.setTextColor(Color.GREEN);
        } else if (s == 3) {
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
            bluetoothService.connect(dispositivo);
        }
    }

    public boolean mostrarMensaje(String mensaje, boolean tipo, String tipo_mensaje) {
        msgArrayAdapter.add(new ChatMessage(tipo, mensaje, tipo_mensaje));
        textoMensaje.setText("");
        return true;
    }

    private void prepararObjMensaje(String mensaje) throws IOException {
        if (mensaje.length() > 0) { // Siempre y cuando no estè vacio
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
            String fecha = simpleDateFormat.format(new Date());
            if (bluetoothService.getState() != STATE_CONNECTED) { // Si no hay conexión
                Toast.makeText(this, R.string.LOST_CONNECTION, Toast.LENGTH_SHORT).show();
                datos_msg = new Object[]{
                        "null",             // ID_MENSAJE
                        nombre_destino +
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
                        1,                   // MOSTRAR
                };

                // Si no hay conexiòn se muestra y se almacena pero no se envìa
                // mostrarMensaje(datos_msg[4].toString() + " \n " + simpleDateFormat.format(new Date()) ,
                // true, datos_msg[3].toString());
                mostrarMensaje(datos_msg[4].toString(), true, datos_msg[3].toString());
                almacenarMensaje(datos_msg);
                textoMensaje.setText("");
                return;
            }

            // Si hay conexxiòn cambian ciertos paràmetros
            datos_msg = new Object[]{
                    "null",             // ID_MENSAJE
                    nombre_destino +
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
                    1,                   // MOSTRAR
            };

            // Si hay conexiòn se muestra, se almacena y se envìa
            mostrarMensaje(datos_msg[4].toString(), true, datos_msg[3].toString());
            almacenarMensaje(datos_msg);
            enviarMensaje(datos_msg);
            textoMensaje.setText("");
        }
    }

    public void almacenarMensaje(Object[] msg) { // Enviados y no enviados
        entidad_mensaje = new Mensaje(msg[0].toString(), msg[1].toString(), msg[2].toString(), msg[3].toString(),
                msg[4].toString(), (int) msg[5], (int) msg[6], (int) msg[7], msg[8].toString(), msg[9].toString(),
                (int) msg[10], (int) msg[11], (int) msg[12]);
        MensajeDB.Insert(this, entidad_mensaje);
    }

    public void guardarMensajeRecibido(Object[] msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String fecha = simpleDateFormat.format(new Date());
        // Solo si es el primer salto o punto a punto se obtiene:
        if ((int) msg[11] == 1) {
            msg[1] = connectingDevice.getName()+connectingDevice.getAddress(); // Id del chat
            msg[8] = connectingDevice.getAddress(); // Direcciòn destino
        }
        entidad_mensaje = new Mensaje(msg[0].toString(), msg[1].toString(), fecha, msg[3].toString(),
                msg[4].toString(), (int) msg[5], (int) msg[6], (int) msg[7], msg[8].toString(), msg[9].toString(),
                0, (int) msg[11], (int) msg[12]);
        MensajeDB.Insert(this, entidad_mensaje);
    }

    public void mostrarConversacion() {
        List<Mensaje> mensajes = MensajeDB.getAllMessages(this, nombre_destino+direccion_destino);
        Iterator<Mensaje> iterator = mensajes.iterator();
        while (iterator.hasNext()) {
            Mensaje msg = iterator.next();
            if (msg.getMostrar() == 1) {
                if (msg.EsMio() == 1) {
                    mostrarMensaje(msg.getContent(), true, msg.getType());
                } else {
                    mostrarMensaje(msg.getContent(), false, msg.getType());
                }
            }
        }
    }

    public void enviarMensaje(Object[] datos_msg) {

        try {
            byte[] bytes_completos = serialize(datos_msg);  // CONVERSION JSON A BYTE
            int tamanoSubArray = 400;
            bluetoothService.write(String.valueOf(bytes_completos.length).getBytes(), datos_msg[3].toString());
            Toast.makeText(this, String.valueOf(bytes_completos.length).getBytes().toString(), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < bytes_completos.length; i += tamanoSubArray) {
                byte[] tempArray;
                tempArray = Arrays.copyOfRange(bytes_completos, i,
                        Math.min(bytes_completos.length, i + tamanoSubArray));
                bluetoothService.write(tempArray, datos_msg[3].toString());
            }
            //coonectarDispositivo(direccion_destino);
        } catch (Exception e) {
            Log.i("Error de envío", e.toString());
        }
    }

    public void reintentarEnviarMensajes() {
        List<Mensaje> mensajes = MensajeDB.getAllNotSendMessages(getApplicationContext()); // mensajes no enviados
        Iterator<Mensaje> iterator = mensajes.iterator();
        while (iterator.hasNext()) {
            Mensaje msg = iterator.next();
            if (bluetoothService.getState() == STATE_CONNECTED) { // siempre y cuando haya conexiòn con algùn dispositivo
                if (msg.getContent().length() > 0) {
                    // Condicion si coinciden mac
                    if (msg.getMAC_DESTINO().equals(connectingDevice.getAddress())) {
                        datos_msg = new Object[]{
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
                                1,                           // MOSTRAR
                        };
                    } else {
                        datos_msg = new Object[]{
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
                                0,                           // MOSTRAR
                        };
                    }
                    enviarMensaje(datos_msg);
                }
            }
        }
    }

    public void findByIds() {
        botonEnviar = (Button) findViewById(R.id.btnSend);
        listView = (ListView) findViewById(R.id.listViewMsg);
        textoMensaje = (EditText) findViewById(R.id.txtMsg);
        nombreDispositivo = (TextView) findViewById(R.id.nombre);
        estadoConexion = (TextView) findViewById(R.id.estadoConexion);
        color = (TextView) findViewById(R.id.color);
    }

    public void personalizarActividad() {
        i = getIntent();
        nombre_destino = i.getStringExtra("nombre_destino");
        direccion_destino = i.getStringExtra("direccion_destino");
        nombreDispositivo.setText(nombre_destino + " " + direccion_destino);
    }

    public void notificarMensaje(Object[] datos_msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, ChatIndividualActivity.class);
        intent.putExtra("nombre_destino", "");
        intent.putExtra("direccion_destino", datos_msg[8].toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.icon_notification);
        mBuilder.setContentTitle("Nuevo Mensaje");
        mBuilder.setContentText(datos_msg[4].toString());
        mBuilder.setVibrate(new long[]{100, 250, 100, 500});
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_menu_activity_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reconnect:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                } else {
                    bluetoothService = new BluetoothService(this, handler);
                    coonectarDispositivo(direccion_destino);
                }
                return true;
            case R.id.escoger_imagen:
                escogerImagen();
                return true;
            case R.id.text_1KB:
                ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void escogerImagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "Seleccionar Imagen"), SELECT_PICTURE);
        } else {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Seleccionar Imagen"), SELECT_PICTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        File archivo = new File(data.getData().getPath());
                        Toast.makeText(this, "Tamaño en bytes: " + archivo.length(), Toast.LENGTH_SHORT).show();
                        tipo_mensaje = "imagen";
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                            byte[] imageBytes = baos.toByteArray();
                            String imageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            prepararObjMensaje(imageEncoded);
                            baos.close();
                        } catch (Exception e) {
                            Toast.makeText(this, "Error de imagen  " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al escoger imagen", Toast.LENGTH_SHORT).show();
                }
        }
    }

    // Conversòn de objeto a byte
    public static byte[] serialize(Object[] obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    // Conversòn de byte a objeto
    public static Object[] deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Object[]) is.readObject();
    }
}