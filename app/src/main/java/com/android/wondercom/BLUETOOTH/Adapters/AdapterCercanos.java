package com.android.wondercom.BLUETOOTH.Adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wondercom.R;

public class AdapterCercanos extends RecyclerView.Adapter<AdapterCercanos.MyViewHolder> {

    private String[] mDataset;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView nombre;
        public MyViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.cv);
            nombre = (TextView) v.findViewById(R.id.name_tv);
        }
    }

    public AdapterCercanos(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public AdapterCercanos.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_cercanos, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.nombre.setText(mDataset[position]);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentValue = mDataset[position];
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}