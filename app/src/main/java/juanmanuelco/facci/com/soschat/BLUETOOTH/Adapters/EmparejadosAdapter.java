package juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import juanmanuelco.facci.com.soschat.R;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Activities.ChatActivity;

public class EmparejadosAdapter extends RecyclerView.Adapter<EmparejadosAdapter.MyViewHolder>{

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice connectingDevice;
    private Context context;
    private String[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView nombre;
        public TextView direccion;
        public MyViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.cv);
            nombre = (TextView) v.findViewById(R.id.name_tv);
            direccion = (TextView) v.findViewById(R.id.ip_tv);
        }
    }

    public EmparejadosAdapter(Context c, String[] myDataset) {
        context = c;
        mDataset = myDataset;
    }

    @Override
    public EmparejadosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_cercanos, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String nombre_destino =  mDataset[position].substring(0, mDataset[position].length() - 17);
        final String direccion_destino = mDataset[position].substring(mDataset[position].length() - 17, mDataset[position].length());
        holder.nombre.setText(nombre_destino);
        holder.direccion.setText(direccion_destino);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bt = new Intent(context, ChatActivity.class);
                bt.putExtra("nombre_destino", nombre_destino);
                bt.putExtra("direccion_destino", direccion_destino);
                context.startActivity(bt);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}