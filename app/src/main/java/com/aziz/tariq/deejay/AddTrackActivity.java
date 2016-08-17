package com.aziz.tariq.deejay;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AddTrackActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    InputStream in;
    int albumId;

    private ListView trackList;
    private AddTracksAdapter mAdapter;
    private List<Track> mListItems;

    MenuItem mNextButton;
    StorageReference storageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;

    String mStashionName;
    String mUserName;

    static int trackIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        loadIndex();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://stashion-5738d.appspot.com");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        mStashionName = getIntent().getStringExtra("stashionName");

        mUserName = getIntent().getStringExtra("username");

        final long TEN_MEGABYTE = 10*1024*1024;

        mListItems = new ArrayList<Track>();

        trackList = (ListView)findViewById(R.id.track_list_view);
        mAdapter = new AddTracksAdapter(this, R.layout.track_list_row, mListItems);
        trackList.setAdapter(mAdapter);

        trackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelected(position);
            }
        });

        ContentResolver cr = this.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION };

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";

        final Cursor cursor = getApplicationContext().getContentResolver().query(uri,
                cursor_cols, where, null, null);

        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            //Log.v("ARTIST", artist);
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            //Log.v("ALBUM", album);
            String trackName = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            //Log.v("TRACK", trackName);
            long trackId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            //Log.v("PATH", data);
            Long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            //Log.v("ALBUM_ID", String.valueOf(albumId));

            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            //Log.v("DURATION", String.valueOf(duration));

            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

            //Logger.debug(albumArtUri.toString());

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                // bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.audio_file);
            } catch (IOException e) {

                e.printStackTrace();
            }

            Track track = new Track();
            track.artist = artist;
            track.albumTitle = album;
            track.trackName = trackName;
            track.path = data;
            track.albumId=albumId;
            track.duration = duration;
            track.coverArt = bitmap;
            track.serviceTrackId = trackId;

            mListItems.add(track);
            mAdapter.notifyDataSetChanged();


            /*
            AudioListModel audioListModel = new AudioListModel();
            audioListModel.setArtist(artist);
            audioListModel.setBitmap(bitmap);
            audioListModel.setAlbum(album);
            audioListModel.setTrack(track);
            audioListModel.setData(data);
            audioListModel.setAlbumId(albumId);
            audioListModel.setDuration(duration);
            audioListModel.setAlbumArtUri(albumArtUri);

            audioArrayList.add(audioListModel);
            */

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_track_activity, menu);
        mNextButton = menu.findItem(R.id.next_button);
        //mNextButton.setTitle(isFinalStep ? R.string.done : R.string.next);
        mNextButton.setTitle("DONE");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                break;
            case R.id.next_button:
                trackChosen();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void trackChosen() {
        int chosenTrackID = mAdapter.getSelectedId();
        final Track chosenTrack = mListItems.get(chosenTrackID);
        //chosenTrack.trackID = chosenTrackID;

        final FirebaseTrack firebaseTrack = new FirebaseTrack(chosenTrack.artist, chosenTrack.albumTitle, chosenTrack.trackName,
                 chosenTrack.path, chosenTrack.albumId, chosenTrack.duration, trackIndex, mUserName, chosenTrack.serviceTrackId);

        myRef.child(mStashionName).child(String.valueOf(trackIndex)).setValue(firebaseTrack);
        /*
        myRef.child("tracks").child(String.valueOf(chosenTrackID)).child("artist").setValue(chosenTrack.artist);
        myRef.child("tracks").child(String.valueOf(chosenTrackID)).child("albumTitle").setValue(chosenTrack.albumTitle);
        myRef.child("tracks").child(String.valueOf(chosenTrackID)).child("trackName").setValue(chosenTrack.trackName);
        myRef.child("tracks").child(String.valueOf(chosenTrackID)).child("albumId").setValue(chosenTrack.albumId);
        myRef.child("tracks").child(String.valueOf(chosenTrackID)).child("duration").setValue(chosenTrack.duration);
        */

        Uri audioPath = Uri.fromFile(new File(chosenTrack.path));
        //Log.v("audioPath", String.valueOf(audioPath));

        StorageReference songReference = storageRef.child(mStashionName + "/audio/" + trackIndex + "/" + audioPath.getLastPathSegment());
        //Log.v("songReference", String.valueOf(songReference));

        //Uri audioPath2 = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/Music/fire9.mp4"));
        //StorageReference songReference = storageRef.child("audio/" + audioPath2.getLastPathSegment());
        //Log.v("audioPath2", String.valueOf(audioPath2));


        UploadTask uploadTask = songReference.putFile(audioPath);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Log.v("downloadUrl", String.valueOf(downloadUrl));

                Intent intent = new Intent();

                intent.putExtra("chosenTrackID", chosenTrack.trackID);
                intent.putExtra("firebaseTrack", firebaseTrack);
                trackIndex++;
                saveIndex();

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        StorageReference photoReference = storageRef.child(mStashionName + "/images/" +trackIndex);

        Bitmap bitmap = chosenTrack.coverArt;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask2 = photoReference.putBytes(data);

        uploadTask2.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Log.v("downloadUrlPhoto", String.valueOf(downloadUrl));

            }
        });




    }

    public void loadIndex() {
        Gson gson = new Gson();
        SharedPreferences sp = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);

        //String json = sp.getString("index", null);
        //savedIncrement = sp.getInt("IncrementValue", 0);
        trackIndex = sp.getInt("index", 0);

    }

    public void saveIndex() {
        SharedPreferences sp = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();

        //save our increment value from Account class
        editor.putInt("index", trackIndex);

        editor.commit();
    }
}
