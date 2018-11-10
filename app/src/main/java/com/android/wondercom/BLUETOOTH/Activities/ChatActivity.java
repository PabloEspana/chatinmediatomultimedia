package com.android.wondercom.BLUETOOTH.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.R;


public class ChatActivity extends Activity {

    private EditText edit;
    DB_SOSCHAT db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB_SOSCHAT(this);
        setContentView(R.layout.bt_activity_chat);

        Button button = (Button) findViewById(R.id.sendMessage);
        edit = (EditText) findViewById(R.id.editMessage);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(ChatActivity.this, "Funciona", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
