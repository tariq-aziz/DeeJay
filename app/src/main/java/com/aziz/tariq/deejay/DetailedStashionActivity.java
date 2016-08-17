package com.aziz.tariq.deejay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

//HAVE DOWNLOAD FAB HERE (TO DOWNLOAD THE SONG/STORE IN LOCAL STORAGE), ADD FAB TO ADD SONG, AND USER FAB TO ADD USER (3 SLIDES)
//  - Stashion overview, TrackList, Users/Members
public class DetailedStashionActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_TRACK = 1;
    private int REQUEST_VIEW_TRACK_LIST = 2;

    static private List<Track> mTracksList;
    static private List<String> mUsersList;
    private DetailedTracksAdapter tracksAdapter;

    private MediaPlayer mMediaPlayer;
    static Stashion mStashion;
    String trackName = "";
    String artistName = "";
    Bitmap coverArt;

    private DatabaseReference mDatabase;
    StorageReference storageRef;
    FirebaseStorage storage;
    StorageReference imageReference;

    FirebaseTrack curFirebaseTrack;
    static List<FirebaseTrack> firebaseTracksList;

    static int curPlayingIndex = 0;

    static ImageView coverArtImageView;
    static TextView trackNameTextView;
    static TextView artistNameTextView;
    static TextView uploadUserTextView;

    StorageReference songReference;

    int prevListSize = 0;

    String username;


    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_detailed_stashion);
        setContentView(R.layout.activity_detailed_stashion);

        trackNameTextView = (TextView) findViewById(R.id.track_name);
        artistNameTextView = (TextView) findViewById(R.id.artist_name);
        coverArtImageView = (ImageView) findViewById(R.id.track_image);
        uploadUserTextView = (TextView) findViewById(R.id.upload_user_text_view);

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        Log.v("ONCREATE", "success");

        Bundle b = getIntent().getExtras();
        mStashion = b.getParcelable("stashion");

        username = getIntent().getStringExtra("username");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://stashion-5738d.appspot.com");

        firebaseTracksList = new ArrayList<FirebaseTrack>();

        //SHOULD I BE GETTING CHILD?
        mDatabase = FirebaseDatabase.getInstance().getReference(mStashion.getName());


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Log.v("ON_DATA_CHANGE", "success");

                if (firebaseTracksList.size() > 0) {
                    prevListSize = 1;
                }

                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();


                while (iterator.hasNext()) {
                    final FirebaseTrack firebaseTrack = iterator.next().getValue(FirebaseTrack.class);

                    int isNewTrack = 1;

                    for (int i = 0; i < firebaseTracksList.size(); i++) {
                        if (firebaseTracksList.get(i).trackID == firebaseTrack.trackID) {
                            isNewTrack = 0;
                        }
                    }
                    if (isNewTrack == 1) {
                        firebaseTracksList.add(firebaseTrack);

                        int TWENTY_MEGABYTE = 20 * 1024 * 1024;

                        Uri audioPath = Uri.fromFile(new File(firebaseTrack.path));

                        songReference = storageRef.child(mStashion.getName() + "/audio/" + firebaseTrack.trackID + "/" + audioPath.getLastPathSegment());

                        songReference.getBytes(TWENTY_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                try {
                                    Log.v("SONG_RETRIEVED", "success");

                                    // create temp file that will hold byte array
                                    File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
                                    tempMp3.deleteOnExit();
                                    FileOutputStream fos = new FileOutputStream(tempMp3);
                                    fos.write(bytes);
                                    fos.close();

                                    FileInputStream fis = new FileInputStream(tempMp3);

                                    musicSrv.addToList(fis.getFD());


                                } catch (IOException e) {
                                    Log.v("NOPE", "failed");
                                    e.printStackTrace();
                                }


                            }
                        });


                    }
                }

                if (prevListSize == 0 && firebaseTracksList.size() != 0) {
                    initializeLayout();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();


    }

    private void initializeLayout() {

        /*
        Log.v("INIT_LAYOUT", "success");

        curFirebaseTrack = firebaseTracksList.get(curPlayingIndex);


        trackNameTextView = (TextView)findViewById(R.id.track_name);
        artistNameTextView = (TextView)findViewById(R.id.artist_name);
        coverArtImageView = (ImageView)findViewById(R.id.track_image);
        uploadUserTextView = (TextView)findViewById(R.id.upload_user_text_view);


        trackNameTextView.setText(curFirebaseTrack.trackName);
        artistNameTextView.setText(curFirebaseTrack.artist);
        uploadUserTextView.setText("Uploaded by " + curFirebaseTrack.uploadUserName);

        int TEN_MEGABYTE = 10 * 1024 * 1024;


        Log.v("RETREIVE_IMG", "success");
        imageReference = storageRef.child(mStashion.getName() + "/images/" + curFirebaseTrack.trackID);
        Log.v("IMG_REFERENCE", String.valueOf(imageReference));

        imageReference.getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                Log.v("INIT_LAYOUT_2", "success");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                coverArtImageView.setImageBitmap(bitmap);

                Log.v("INIT_LAYOUT_3", "success");
            }
        });

    */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

        }
    }

    public void viewTrackList(View v) {
        Intent intent = new Intent(DetailedStashionActivity.this, TrackListActivity.class);
        intent.putExtra("stashion", mStashion);
        intent.putExtra("username", username);
        startActivityForResult(intent, REQUEST_VIEW_TRACK_LIST);
    }

    public void skipTrack(View v) {

        if (firebaseTracksList.size() > 0) {

            musicSrv.setSong(++curPlayingIndex);
            musicSrv.playSong();

        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            //musicSrv.setList(firebaseTracksList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    public static void setLayout(int index) {


        curPlayingIndex = index;
        Log.v("INIT_LAYOUT", "success");

        FirebaseTrack curFirebaseTrack = firebaseTracksList.get(index);


        trackNameTextView.setText(curFirebaseTrack.trackName);
        artistNameTextView.setText(curFirebaseTrack.artist);
        uploadUserTextView.setText("Uploaded by " + curFirebaseTrack.uploadUserName);

        int TEN_MEGABYTE = 10 * 1024 * 1024;


        Log.w("RETREIVE_IMG", "success");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://stashion-5738d.appspot.com");

        StorageReference imageReference = storageRef.child(mStashion.getName() + "/images/" + curFirebaseTrack.trackID);
        Log.v("IMG_REFERENCE", String.valueOf(imageReference));


        imageReference.getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                Log.v("INIT_LAYOUT_2", "success");

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                coverArtImageView.setImageBitmap(bitmap);

            }
        });

    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }
}



