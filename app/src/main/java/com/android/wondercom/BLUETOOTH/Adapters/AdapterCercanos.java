package com.android.wondercom.BLUETOOTH.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wondercom.BLUETOOTH.Activities.ChatActivity;
import com.android.wondercom.BLUETOOTH.MainActivityBT;
import com.android.wondercom.InicioActivity;
import com.android.wondercom.MainActivity;
import com.android.wondercom.R;

public class AdapterCercanos extends RecyclerView.Adapter<AdapterCercanos.MyViewHolder> {

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

    public AdapterCercanos(Context c, String[] myDataset) {
        context = c;
        mDataset = myDataset;
    }

    @Override
    public AdapterCercanos.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_cercanos, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.nombre.setText(
                mDataset[position].substring(0, mDataset[position].length() - 17)
        );
        holder.direccion.setText(
                mDataset[position].substring(mDataset[position].length() - 17, mDataset[position].length())
        );
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bt = new Intent(context, ChatActivity.class);
                context.startActivity(bt);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}