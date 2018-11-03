package com.android.wondercom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ActivityBluetooth extends AppCompatActivity {

    private TextView status;
    private Button btnConnect;
    private ListView listView;
    private Dialog dialog;
    private TextInputLayout inputLayout;  // texto a enviar
    private ArrayAdapter<String> chatAdapter;  // adaptador de mensajes
    private ArrayList<String> chatMessages;  // arreglo de mensajes
    private BluetoothAdapter bluetoothAdapter;  // adaptador bluetooth

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ControladorBluetooth controladorBluetooth;  // clase chat controller
    private BluetoothDevice connectingDevice;  //
    private ArrayAdapter<String> discoveredDevicesAdapter;

    private static final int SELECT_PICTURE = 43; // código de resultado al escoger imagen
    private ImageView imagen;
    private boolean hay_imagen = false;
    private Bitmap bitmap;
    String tipo_mensaje = "texto";
    Byte[] buffer_img = null;
    int nummero_de_bytes = 0;
    int index = 0;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        findViewsByIds();

        //Comprueba si el dispositivo soporta o no Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no disponible!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Muestra un dialogo de dispositivos Bluetooth cuando se da click en el botón conectar
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterPickDialog();
            }
        });

        //set chat adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        listView.setAdapter(chatAdapter);

    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:  // Si es mensaje de cambio de estado
                    switch (msg.arg1) {
                        case ControladorBluetooth.STATE_CONNECTED:  // Si es estado conectado
                            setStatus("Conectado a: " + connectingDevice.getName());  // Se envía el mensaje como parámetro
                            btnConnect.setEnabled(false);  // Se deshabilita el botón de Conectar
                            break;
                        case ControladorBluetooth.STATE_CONNECTING:  // Si es estado conectando
                            setStatus("Conectando...");  // Se envía el mensaje como parámetro
                            btnConnect.setEnabled(false);  // Se deshabilita el botón de Conectar
                            break;
                        case ControladorBluetooth.STATE_LISTEN:
                        case ControladorBluetooth.STATE_NONE:
                            setStatus("No conectado");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:    // Si es mensaje escrito
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);  // Se almacena el mensaje a mostrar
                    chatMessages.add("Yo: " + writeMessage);        // Se agrega el mensaje al arreglo de mensajes
                    chatAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ:      // Si es mensaje lectura
                    /*byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
                    chatAdapter.notifyDataSetChanged();
                    break;*/
                    byte[] readBuf = (byte[]) msg.obj;
                    try{
                        Bitmap bm = BitmapFactory.decodeByteArray(readBuf, 0, msg.arg1);
                        bm = Bitmap.createScaledBitmap(bm,  600 ,600, true);
                        imagen.setImageBitmap(bm);
                    }catch (Exception e){
                        Toast.makeText(ActivityBluetooth.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
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

    private void showPrinterPickDialog() {
        dialog = new Dialog(this);      // Se crea el cuadro de diálogo de dispositivos Bluetooth
        dialog.setContentView(R.layout.layout_bluetooth);  // se envía el layout al cuadro de diálogo
        dialog.setTitle("Dispositivos Bluetooth");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    private void setStatus(String s) {
        status.setText(s);
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        controladorBluetooth.connect(device);
    }

    private void findViewsByIds() {
        status = (TextView) findViewById(R.id.status);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        listView = (ListView) findViewById(R.id.list);
        inputLayout = (TextInputLayout) findViewById(R.id.input_layout);
        View btnSend = findViewById(R.id.btn_send);
        View btnSendImg = findViewById(R.id.btn_sendImg);
        imagen = (ImageView)findViewById(R.id.imgSelect);

        btnSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipo_mensaje.equals("texto")){
                    if (inputLayout.getEditText().getText().toString().equals("")) {
                        Toast.makeText(ActivityBluetooth.this, "Escriba un mensaje por favor", Toast.LENGTH_SHORT).show();
                    } else {
                        sendMessage(inputLayout.getEditText().getText().toString());
                        inputLayout.getEditText().setText("");
                    }
                }
                else if (tipo_mensaje.equals("imagen")){
                    try{

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                        byte[] imageBytes = stream.toByteArray();
                        int subArraySize = 400;
                        sendMultimediaMessage(String.valueOf(imageBytes.length).getBytes());
                        for(int i = 0; i < imageBytes.length; i+=subArraySize){
                            byte[] tempArray;
                            tempArray = Arrays.copyOfRange(imageBytes, i,  Math.min(imageBytes.length, i+subArraySize));
                            sendMultimediaMessage(tempArray);
                        }


                        /* Segunda forma de envío
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                        byte[] image = bytes.toByteArray();
                        sendMultimediaMessage(String.valueOf(image.length).getBytes());*/

                    }catch (Exception e){
                        Toast.makeText(ActivityBluetooth.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    controladorBluetooth = new ControladorBluetooth(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth no se pudo habilitar, cerrando apñlicación", Toast.LENGTH_SHORT).show();
                    finish();
                };
            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK){
                    if(data != null){
                        tipo_mensaje = "imagen";
                        try{
                            Uri imgUri = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                            bitmap = Bitmap.createScaledBitmap(bitmap,  600 ,600, true);
                            imagen.setImageBitmap(bitmap);
                        }catch (Exception e){
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(this, "Error al escoger imagen", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void sendMessage(String message) {
        if (controladorBluetooth.getState() != ControladorBluetooth.STATE_CONNECTED) {
            Toast.makeText(this, "¡Connexión perdida!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            controladorBluetooth.write(send, tipo_mensaje);
        }
    }

    private void sendMultimediaMessage(byte[] image) {
        if (controladorBluetooth.getState() != ControladorBluetooth.STATE_CONNECTED) {
            Toast.makeText(this, "¡Connexión perdida!", Toast.LENGTH_SHORT).show();
            return;
        }
        controladorBluetooth.write(image, tipo_mensaje);
        tipo_mensaje = "texto";
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            controladorBluetooth = new ControladorBluetooth(this, handler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (controladorBluetooth != null) {
            if (controladorBluetooth.getState() == ControladorBluetooth.STATE_NONE) {
                controladorBluetooth.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (controladorBluetooth != null)
            controladorBluetooth.stop();
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };

}


