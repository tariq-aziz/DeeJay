package com.aziz.tariq.deejay;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tariqaziz on 2016-06-28.
 */
public class FirebaseTrack implements Parcelable {

    public String artist;
    public String albumTitle;
    public String trackName;
    public String path;
    public long albumId;
    public int duration;
    public int trackID;
    public String uploadUserName;
    long serviceTrackId;
    public int isPlaying=0;

    public FirebaseTrack(){

    }

    public FirebaseTrack(String artist, String albumTitle, String trackName, String path, long albumId, int duration, int trackID,
                         String uploadUserName, long serviceTrackId) {
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.trackName = trackName;
        this.path = path;
        this.albumId = albumId;
        this.duration = duration;
        this.trackID = trackID;
        this.uploadUserName = uploadUserName;
        this.serviceTrackId = serviceTrackId;

    }

    protected FirebaseTrack(Parcel in) {
        artist = in.readString();
        albumTitle = in.readString();
        trackName = in.readString();
        path = in.readString();
        albumId = in.readLong();
        duration = in.readInt();
        trackID = in.readInt();
        uploadUserName = in.readString();
        serviceTrackId = in.readLong();
    }

    public static final Creator<FirebaseTrack> CREATOR = new Creator<FirebaseTrack>() {
        @Override
        public FirebaseTrack createFromParcel(Parcel in) {
            return new FirebaseTrack(in);
        }

        @Override
        public FirebaseTrack[] newArray(int size) {
            return new FirebaseTrack[size];
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
        dest.writeInt(trackID);
        dest.writeString(uploadUserName);
        dest.writeLong(serviceTrackId);
    }
}
