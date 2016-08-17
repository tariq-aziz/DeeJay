package com.aziz.tariq.deejay;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tariqaziz on 2016-06-27.
 */
public class Track implements Parcelable{
    public String artist;
    public String albumTitle;
    public String trackName;
    public String path;
    public long albumId;
    public int duration;
    public Bitmap coverArt;
    public boolean isSelected;
    public int trackID;
    public long serviceTrackId;

    public Track(){

    }
    public Track(String artist, String albumTitle, String trackName, String path, long albumId, int duration, Bitmap coverArt,
                 long serviceTrackId){
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.trackName = trackName;
        this.path = path;
        this.albumId = albumId;
        this.duration = duration;
        this.coverArt = coverArt;
        this.serviceTrackId = serviceTrackId;
    }


    protected Track(Parcel in) {
        artist = in.readString();
        albumTitle = in.readString();
        trackName = in.readString();
        path = in.readString();
        albumId = in.readLong();
        duration = in.readInt();
        coverArt = in.readParcelable(Bitmap.class.getClassLoader());
        isSelected = in.readByte() != 0;
        serviceTrackId = in.readLong();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(albumTitle);
        dest.writeString(trackName);
        dest.writeString(path);
        dest.writeLong(albumId);
        dest.writeInt(duration);
        dest.writeParcelable(coverArt, flags);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeLong(serviceTrackId);
    }

}
