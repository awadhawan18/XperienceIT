package com.xperienceit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

import com.xperienceit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList<XperienceObject> xperiences_list;
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<XperienceObject> xperiences_list) {
        this.xperiences_list = xperiences_list;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.xperience_card, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final XperienceObject temp = xperiences_list.get(position);
        holder.xperience_name.setText(temp.getName());
        holder.xpeience_description.setText(temp.getSmallDescription());
        holder.xperience_price.setText("Rs." + String.format("%.2f", Helper.getPrice(temp.getPrice(), temp.getDiscount())));
        Picasso.with(mContext).load(encodeUrl(temp.getPhotoUrl().get(0))).resize(800, 0).into(holder.xperience_image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, XperienceDetailsActivity.class);
                intent.putExtra("details", temp);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return xperiences_list.size();
    }

    public String encodeUrl(String str) {
        return str.replace(" ", "%20");
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView xperience_name, xpeience_description, xperience_price;
        private ImageView xperience_image;
        private Context mContext;

        public MyViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            xperience_name = itemView.findViewById(R.id.xperience_name);
            xpeience_description = itemView.findViewById(R.id.xperience_description);
            xperience_price = itemView.findViewById(R.id.xperience_price);
            xperience_image = itemView.findViewById(R.id.xperience_image);


        }


    }
}
