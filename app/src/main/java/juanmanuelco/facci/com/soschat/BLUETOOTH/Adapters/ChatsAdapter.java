package juanmanuelco.facci.com.soschat.BLUETOOTH.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import juanmanuelco.facci.com.soschat.BLUETOOTH.Activities.ChatIndividualActivity;
import juanmanuelco.facci.com.soschat.BLUETOOTH.Entidades.Chat;
import juanmanuelco.facci.com.soschat.R;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder>{
    private Context context;
    private String[] mDataset;
    Chat entidad_chat;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView nombre;
        public TextView direccion;
        public MyViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.cv2);
            nombre = (TextView) v.findViewById(R.id.name_tv2);
            direccion = (TextView) v.findViewById(R.id.ip_tv2);
        }
    }

    public ChatsAdapter(Context c, String[] myDataset) {
        context = c;
        mDataset = myDataset;
    }

    @Override
    public ChatsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_chats, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ChatsAdapter.MyViewHolder vh = new ChatsAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChatsAdapter.MyViewHolder holder, final int position) {
        //final String nombre_destino =  mDataset[position].substring(0, mDataset[position].length() - 17);
        //final String direccion_destino = mDataset[position].substring(mDataset[position].length() - 17, mDataset[position].length());
        final String nombre_destino =  mDataset[position];
        final String direccion_destino = mDataset[position];
        holder.nombre.setText(nombre_destino);
        holder.direccion.setText(direccion_destino);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bt = new Intent(context, ChatIndividualActivity.class);
                bt.putExtra("nombre_destino", nombre_destino);
                bt.putExtra("direccion_destino", direccion_destino);
                context.startActivity(bt);
            }
        });
    }

    public int getItemCount() {
        return mDataset.length;
    }

}
