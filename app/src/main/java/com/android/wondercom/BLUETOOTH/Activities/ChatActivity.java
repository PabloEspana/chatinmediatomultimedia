package com.android.wondercom.BLUETOOTH.Activities;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.wondercom.BLUETOOTH.Adapters.ChatArrayAdapter;
import com.android.wondercom.BLUETOOTH.Entities.ChatMessage;
import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.R;


public class ChatActivity extends Activity {

    private EditText edit;
    DB_SOSCHAT db;

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB_SOSCHAT(this);
        setContentView(R.layout.bt_activity_chat);

        buttonSend = (Button) findViewById(R.id.btnSend);
        listView = (ListView) findViewById(R.id.listViewMsg);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.bt_element_right_msg);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.txtMsg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

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
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        side = !side;
        return true;
    }
}
