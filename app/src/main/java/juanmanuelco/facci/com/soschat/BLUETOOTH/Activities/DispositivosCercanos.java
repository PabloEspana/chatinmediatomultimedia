package juanmanuelco.facci.com.soschat.BLUETOOTH.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import juanmanuelco.facci.com.soschat.R;

public class DispositivosCercanos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_dispositivos_cercanos);
        setTitle(getString(R.string.DISCOVERED_DEVICES));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
