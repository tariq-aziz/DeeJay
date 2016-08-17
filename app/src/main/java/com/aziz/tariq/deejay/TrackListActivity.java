package com.aziz.tariq.deejay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrackListActivity extends AppCompatActivity {

    DetailedTracksAdapter mAdapter;
    ListView tracksListView;
    static List<FirebaseTrack> firebaseTrackList;
    private static final int REQUEST_ADD_TRACK = 1;
    private DatabaseReference mDatabase;
    Stashion mStashion;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);

        //TODO: save firebaseTrackList in sharedPrefs

        firebaseTrackList = new ArrayList<FirebaseTrack>();

        mUsername = getIntent().getStringExtra("username");

        Bundle b = getIntent().getExtras();
        mStashion = b.getParcelable("stashion");

        tracksListView = (ListView)findViewById(R.id.tracks_list_view);
        mAdapter = new DetailedTracksAdapter(this, firebaseTrackList);
        tracksListView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference(mStashion.getName());


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                firebaseTrackList.clear();

                Log.v("LIST_DATA_CHANGED", "success");

                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();


                while (iterator.hasNext()) {
                    final FirebaseTrack firebaseTrack = iterator.next().getValue(FirebaseTrack.class);


                        Log.v("ADDED_TRACK_NAME", firebaseTrack.trackName);
                        firebaseTrackList.add(firebaseTrack);
                        mAdapter.notifyDataSetChanged();


                    //it works, but adds too many


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            /*
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterable.iterator();


                    while (iterator.hasNext()) {
                        final FirebaseTrack firebaseTrack = iterator.next().getValue(FirebaseTrack.class);

                        int isNewTrack = 1;

                        for(int i=0; i<firebaseTrackList.size(); i++){
                            if(firebaseTrackList.get(i).trackID==firebaseTrack.trackID){
                                //LOG THESE VALUES, firebaseTrack may have wrong ID
                                isNewTrack = 0;
                            }
                        }
                        if(isNewTrack==1){
                            firebaseTrackList.add(firebaseTrack);
                            mAdapter.notifyDataSetChanged();
                        }


                        //it works, but adds too many
                        Log.v("TRACK_NAME_2", firebaseTrack.trackName);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });
            */


            /*
            FirebaseTrack firebaseTrack = data.getExtras().getParcelable("firebaseTrack");
            firebaseTrackList.add(firebaseTrack);
            mAdapter.notifyDataSetChanged();
            */

            /*
            Bundle b = data.getExtras();
            Track track = b.getParcelable("chosenTrack");


            mTracksList.add(track);

            initializeLayout();
            */
        }
    }

    public void addTrack(View v){
        Intent intent = new Intent(TrackListActivity.this, AddTrackActivity.class);
        intent.putExtra("stashionName", mStashion.getName());
        intent.putExtra("username", mUsername);
        startActivityForResult(intent, REQUEST_ADD_TRACK);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
