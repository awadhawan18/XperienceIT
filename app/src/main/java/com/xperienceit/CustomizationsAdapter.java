package com.xperienceit;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomizationsAdapter extends RecyclerView.Adapter<CustomizationsAdapter.CustomisationHolder> {
    private ArrayList<HashMap<String, String>> customization_list;
    private Context mContext;

    public CustomizationsAdapter(Context mContext, ArrayList<HashMap<String, String>> customization_list) {
        this.customization_list = customization_list;
        this.mContext = mContext;
    }

    @Override
    public CustomizationsAdapter.CustomisationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.customization_card, parent, false);
        return new CustomizationsAdapter.CustomisationHolder(listItem);

    }

    @Override
    public void onBindViewHolder(final CustomizationsAdapter.CustomisationHolder holder, final int position) {
        final HashMap<String, String> temp = customization_list.get(position);
        holder.custom_name.setText(temp.get("name"));
        holder.custom_desc.setText(temp.get("description"));
        holder.custom_price.setText("Rs." + temp.get("price"));
        Picasso.with(mContext).load(encodeUrl(temp.get("photo"))).resize(800, 0).into(holder.custom_image);
        holder.custom_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("switch no", String.valueOf(holder.getAdapterPosition()));
                notifyDataSetChanged();
                if (isChecked) {
                    temp.put("switch", "true");
                } else {
                    temp.put("switch", "false");
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return customization_list.size();
        //deal_details.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public String encodeUrl(String str) {
        return str.replace(" ", "%20");
    }

    public static class CustomisationHolder extends RecyclerView.ViewHolder {
        public TextView custom_name, custom_desc, custom_price;
        public ImageView custom_image;
        public Switch custom_switch;
        public Context mContext;

        public CustomisationHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            custom_name = itemView.findViewById(R.id.customization_name);
            custom_desc = itemView.findViewById(R.id.customization_desc);
            custom_price = itemView.findViewById(R.id.customization_price);
            custom_image = itemView.findViewById(R.id.customization_photo);
            custom_switch = itemView.findViewById(R.id.customization_switch);


        }

    }
}
