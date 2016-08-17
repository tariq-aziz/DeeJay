package com.aziz.tariq.deejay;

import android.content.Context;
import android.graphics.Color;
import android.net.MailTo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

/**
 * Created by tariqaziz on 2016-06-20.
 */
public class AddTracksAdapter extends ArrayAdapter<Track> {
    int mCurrSelected=-1;

    private Context mContext;
    private List<Track> mTracks;

    public AddTracksAdapter(Context context, int resource, List<Track> tracks){
        super(context, resource, tracks);
        mContext = context;
        mTracks = tracks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View currView = super.getView(position, convertView, parent);
        Track track = (Track) getItem(position);


        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.track_list_row, parent, false);
            holder = new ViewHolder();
            holder.container = (RelativeLayout) convertView.findViewById(R.id.container);
            holder.trackTitleTextView = (TextView) convertView.findViewById(R.id.track_title);
            holder.artistTextView = (TextView)convertView.findViewById(R.id.track_artist);
            holder.durationTextView = (TextView)convertView.findViewById(R.id.track_duration);
            holder.albumImageView = (ImageView) convertView.findViewById(R.id.track_image);
            convertView.setTag(holder);
        }

        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.trackTitleTextView.setText(track.trackName);
        holder.artistTextView.setText(track.artist);
        holder.durationTextView.setText(String.valueOf(track.duration));
        holder.albumImageView.setImageBitmap(track.coverArt);

        if (track.isSelected) {
            holder.container.setBackgroundColor(Color.argb(70, 0,0,0));
            Log.v("SELECTED", "Success");
        }
        else{

            holder.container.setBackgroundResource(R.drawable.ripple_effect);
        }
        // Trigger the download of the URL asynchronously into the image view.
        //Picasso.with(mContext).load(track.getArtworkURL()).into(holder.trackImageView);

        return convertView;
    }

    public int getSelectedId(){
        return mCurrSelected;
    }


    //Setting the item in the argumented position as selected.
    public void setSelected(int position) {
        // The -1 value means that no item is selected
        if (mCurrSelected != -1) {
            mTracks.get(mCurrSelected).isSelected = false;
            Log.v("mCurrSelected", String.valueOf(mCurrSelected));
            Log.v("isItemSelected", String.valueOf(mTracks.get(mCurrSelected).isSelected));
        }

        // Selecting the item in the position we got as an argument
        if (position != -1) {
            mTracks.get(position).isSelected = true;
            mCurrSelected = position;
        }

        // Making the list redraw
        notifyDataSetChanged();

    }



    static class ViewHolder {
        RelativeLayout container;
        ImageView albumImageView;
        TextView trackTitleTextView;
        TextView artistTextView;
        TextView durationTextView;
    }
}
