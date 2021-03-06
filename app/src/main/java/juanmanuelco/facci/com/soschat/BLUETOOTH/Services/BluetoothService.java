package juanmanuelco.facci.com.soschat.BLUETOOTH.Services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.xml.transform.stream.StreamResult;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Activities.ChatIndividual;

public class BluetoothService {

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private static final String APP_NAME = "BluetoothChatApp";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    public String type_send = "";
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ReadWriteThread connectedThread;
    private int state;


    public BluetoothService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler; // Se asigna el manejador
    }

    // Para obtener el estado de conexión actual
    public synchronized int getState() {
        return state;
    }

    // Para asignar el estado actual de la conexión del chat
    private synchronized void setState(int state) {
        this.state = state;
        handler.obtainMessage(ChatIndividual.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    // START SERVICE
    public synchronized void start() {
        // Cancelar cualquier hilo
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancelar cualquier hilo en ejecución
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN); // envia estado escuchando  para aceptar hilo
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    // Inicia conexión a un dispositivo remoto
    public synchronized void connect(BluetoothDevice device) {
        // Cancela cualquier hilo
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel hilo corriendo o en ejecucion
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Inicia el hilo para conectar con el dispositivo
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }


    // administrar la conexión Bluetooth
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        // Cancel the thread
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel running thread
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Inicia el hilo para gestionar la conexión y realizar transmisiones.
        connectedThread = new ReadWriteThread(socket);
        connectedThread.start();

        // Envia el nombre del dispositivo conectado a la actividad de la interfaz de usuario
        Message msg = handler.obtainMessage(ChatIndividual.MESSAGE_DEVICE_OBJECT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ChatIndividual.DEVICE_OBJECT, device);
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    // detiene todos los hilos
    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        setState(STATE_NONE);
    }


    public void write(byte[] out) {
        ReadWriteThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED)
                return;
            r = connectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        Message msg = handler.obtainMessage(ChatIndividual.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "No se puede conectar al dispositivo");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Inicia el servicio para reiniciar el modo de escucha.
        BluetoothService.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(ChatIndividual.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Conexión perdida con el dispositivo");
        msg.setData(bundle);
        handler.sendMessage(msg);

        // Inicia el servicio para reiniciar el modo de escucha.
        BluetoothService.this.start();
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket;
            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }

        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run() {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            // Start the connected thread
            connected(socket, device);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ReadWriteThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ReadWriteThread(BluetoothSocket socket) {
            this.bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);
                    // Send the obtained bytes to the UI Activity
                    handler.obtainMessage(ChatIndividual.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothService.this.start();
                    break;
                }
            }

        /*public void run() {

            byte[] buffer = null;
            int numberOfBytes = 0;
            int index = 0;
            boolean flag = true;

            while (true) {
                try {
                    if (flag) {
                        byte[] temp = new byte[inputStream.available()];
                        if (inputStream.read(temp) > 0) {
                            numberOfBytes = Integer.parseInt(new String(temp, "UTF-8"));
                            buffer = new byte[numberOfBytes];
                            flag = false;
                        }
                    } else {
                        byte[] data = new byte[inputStream.available()];
                        int numbers = inputStream.read(data);
                        System.arraycopy(data, 0, buffer, index, numbers);
                        index = index + numbers;
                        if (index == numberOfBytes) {
                            handler.obtainMessage(ChatIndividual.MESSAGE_READ, numberOfBytes, -1, buffer).sendToTarget();
                            flag = true;
                        }

                    }
                } catch (IOException e) {
                    connectionLost();
                    BluetoothService.this.start();
                    break;
                }
            }

            /*if (tipo_msg.equals("texto")) {
                byte[] buffer = new byte[1024];
                int bytes;
                // Keep listening to the InputStream
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = inputStream.read(buffer);
                        // Send the obtained bytes to the UI Activity
                        handler.obtainMessage(ChatIndividual.MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    } catch (IOException e) {
                        connectionLost();
                        // Start the service over to restart listening mode
                        BluetoothService.this.start();
                        break;
                    }
                }
            }*/






/*
            while (true) {
                if (primer_envio) {
                    try {
                        bytes_tipo = inputStream.read(buffer_tipo);
                        if (bytes_tipo == 5) {
                            tipo_msg = "texto";
                        } else if (bytes_tipo == 6) {
                            tipo_msg = "imagen";
                        }
                        primer_envio = false;
                    } catch (IOException e) {
                        connectionLost();
                        BluetoothService.this.start();
                        break;
                    }
                } else{
                    if (tipo_msg.equals("texto")) {
                        byte[] buffer = new byte[1024];
                        int bytes;
                        // Keep listening to the InputStream
                        while (true) {
                            try {
                                // Read from the InputStream
                                bytes = inputStream.read(buffer);
                                // Send the obtained bytes to the UI Activity
                                handler.obtainMessage(ChatIndividual.MESSAGE_READ, bytes, -1, buffer)
                                        .sendToTarget();
                            } catch (IOException e) {
                                connectionLost();
                                // Start the service over to restart listening mode
                                BluetoothService.this.start();
                                break;
                            }
                        }
                    } else if (tipo_msg.equals("imagen")) {
                        byte[] buffer = null;
                        int numberOfBytes = 0;
                        int index = 0;
                        boolean flag = true;

                        while (true) {
                            if (flag) {
                                try {
                                    byte[] temp = new byte[inputStream.available()];
                                    if (inputStream.read(temp) > 0) {
                                        numberOfBytes = Integer.parseInt(new String(temp, "UTF-8"));
                                        buffer = new byte[numberOfBytes];
                                        flag = false;
                                    }
                                } catch (IOException e) {
                                    connectionLost();
                                }
                            } else {
                                try {
                                    byte[] data = new byte[inputStream.available()];
                                    int numbers = inputStream.read(data);

                                    System.arraycopy(data, 0, buffer, index, numbers);
                                    index = index + numbers;

                                    if (index == numberOfBytes) {
                                        Log.d("WWW", "run: " + String.valueOf(buffer));
                                        handler.obtainMessage(ChatIndividual.MESSAGE_READ, numberOfBytes, -1, buffer).sendToTarget();
                                        flag = true;
                                    }
                                } catch (IOException e) {
                                    connectionLost();
                                    break;
                                }
                            }
                        }
                    }
                    //}
                }
            }*/

            /*
            if (type_send.equals("texto")){
                byte[] buffer = new byte[1024];
                int bytes;
                // Keep listening to the InputStream
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = inputStream.read(buffer);
                        // Send the obtained bytes to the UI Activity
                        handler.obtainMessage(ChatIndividual.MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    } catch (IOException e) {
                        connectionLost();
                        // Start the service over to restart listening mode
                        BluetoothService.this.start();
                        break;
                    }
                }
            }*/


            /*byte[] buffer = null;
            int numberOfBytes = 0;
            int index = 0;
            boolean flag = true;

            while (true) {
                if (flag) {
                    try {
                        byte[] temp = new byte[inputStream.available()];
                        if (inputStream.read(temp) > 0) {
                            numberOfBytes = Integer.parseInt(new String(temp, "UTF-8"));
                            buffer = new byte[numberOfBytes];
                            flag = false;
                        }
                    } catch (IOException e) {
                        connectionLost();
                    }
                } else {
                    try {
                        byte[] data = new byte[inputStream.available()];
                        int numbers = inputStream.read(data);

                        System.arraycopy(data, 0, buffer, index, numbers);
                        index = index + numbers;

                        if (index == numberOfBytes) {
                            Log.d("WWW", "run: " + String.valueOf(buffer));
                            handler.obtainMessage(ChatIndividual.MESSAGE_READ, numberOfBytes, -1, buffer).sendToTarget();
                            flag = true;
                        }
                    } catch (IOException e) {
                        connectionLost();
                        break;
                    }
                }
            }*/
        }

        // write to OutputStream
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
                //handler.obtainMessage(ChatIndividual.MESSAGE_WRITE, -1, -1,
                //      buffer).sendToTarget();
            } catch (IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Failed to write bytes \n" + e.toString());
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}