package com.xperienceit;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xperienceit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

//custom girdview adapter with holder view which contains an image and a textview
public class GridAdaptor extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context mContext;
    private ArrayList<HashMap<String, String>> categories;

    public GridAdaptor(Context context, ArrayList<HashMap<String, String>> categories) {

        mContext = context;
        this.categories = categories;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.v("categories in grid", String.valueOf(this.categories));
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //inflates the holder view using View class in each grid space
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tiles_frame_layout, null);
        }

        Holder holder = new Holder(mContext);
        holder.tv = convertView.findViewById(R.id.tile_text_view);
        holder.img = convertView.findViewById(R.id.tile_image_view);
        HashMap<String, String> temp = categories.get(position);
        holder.tv.setText(temp.get("name"));
        holder.tv.setTextColor(Color.WHITE);
        Picasso.with(mContext).load(temp.get("photo")).resize(800, 0).into(holder.img);
        /*RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250);
        holder.img.setLayoutParams(layoutParams);*/

        return convertView;
    }

    public class Holder extends View {
        TextView tv;
        ImageView img;

        public Holder(Context context) {
            super(context);
        }
    }
}
