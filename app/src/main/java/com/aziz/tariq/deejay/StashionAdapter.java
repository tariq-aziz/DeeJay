package com.aziz.tariq.deejay;

import android.content.Context;
import android.net.MailTo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tariqaziz on 2016-06-20.
 */
public class StashionAdapter extends BaseAdapter {

    private Context mContext;
    private List<Stashion> mStashions;

    public StashionAdapter(Context context, List<Stashion> stashions){
        mContext = context;
        mStashions = stashions;
    }

    @Override
    public int getCount() {
        return mStashions.size();
    }

    @Override
    public Object getItem(int position) {
        return mStashions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Stashion stashion = (Stashion) getItem(position);

        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.stashion_item_layout, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.stashion_name);

            holder.trackImageView = (ImageView) convertView.findViewById(R.id.stashion_image);
            convertView.setTag(holder);
        }

        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTextView.setText(stashion.getName());

        // Trigger the download of the URL asynchronously into the image view.
        //Picasso.with(mContext).load(track.getArtworkURL()).into(holder.trackImageView);

        return convertView;
    }


    static class ViewHolder {
        ImageView trackImageView;
        TextView titleTextView;
    }
}
