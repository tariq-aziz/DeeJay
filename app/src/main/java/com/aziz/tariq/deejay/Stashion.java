package com.aziz.tariq.deejay;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tariqaziz on 2016-06-25.
 */
public class Stashion implements Parcelable{
    private String name;
    private String[] users;
    private ArrayList<Track> trackList;
    private int passcode;

    public Stashion(String name, int passcode){
        this.name = name;
        this.passcode = passcode;
    }



    public static final Creator<Stashion> CREATOR = new Creator<Stashion>() {
        @Override
        public Stashion createFromParcel(Parcel in) {
            return new Stashion(in);
        }

        @Override
        public Stashion[] newArray(int size) {
            return new Stashion[size];
        }
    };

    public String getName(){
        return name;
    }

    public List<Track> getTrackList(){
        return trackList;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringArray(users);
        dest.writeList(trackList);
        dest.writeInt(passcode);
    }

    protected Stashion(Parcel in) {
        name = in.readString();
        users = in.createStringArray();
        trackList = in.readArrayList(Track.class.getClassLoader());
        passcode = in.readInt();
    }

    public int getPasscode(){
        return passcode;
    }
}
