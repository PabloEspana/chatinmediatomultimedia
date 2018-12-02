package juanmanuelco.facci.com.soschat.BLUETOOTH.Activities;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Entities.ChatMessage;
import juanmanuelco.facci.com.soschat.R;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters.ChatArrayAdapter;

public class ChatActivity extends AppCompatActivity {

    private EditText edit;
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean lado = false;

    Intent i;
    String nombre_destino;
    String direccion_destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_chat);
        findByIds();
        configurarActividad();

        // Instancia clase chat adapter enviada a listado
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.bt_element_right_msg);
        listView.setAdapter(chatArrayAdapter);

        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        // Evento click al botón enviar
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
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

    private boolean sendChatMessage(){
        chatArrayAdapter.add(new ChatMessage(lado, chatText.getText().toString()));
        chatText.setText("");
        lado = !lado;
        return true;
    }

    // Personalizacción de actividad
    public void configurarActividad(){
        i = getIntent();
        nombre_destino = i.getStringExtra("nombre_destino");
        direccion_destino = i.getStringExtra("direccion_destino");
        this.setTitle(nombre_destino);
        // Cambiar foto
        // Obtener  mensajes de BD y listarlos
    }

    public void findByIds(){
        buttonSend = (Button) findViewById(R.id.btnSend);
        listView = (ListView) findViewById(R.id.listViewMsg);
        chatText = (EditText) findViewById(R.id.txtMsg);
    }

}
