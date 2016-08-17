package com.aziz.tariq.deejay;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DETAILED_STASHION_REQUEST =1 ;
    ListView stashionListView;
    List<Stashion> stashionsList;
    StashionAdapter stashionAdapter;

    public static final int CREATE_STASHION_REQUEST = 2;
    public static final int STASHION_SIGN_IN_REQUEST = 3;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stashionsList = new ArrayList<Stashion>();

        stashionListView = (ListView) findViewById(R.id.available_stashions_list);
        stashionAdapter = new StashionAdapter(this, stashionsList);

        stashionListView.setAdapter(stashionAdapter);

        stashionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, StashionSignInActivity.class);
                intent.putExtra("stashion", stashionsList.get(position));
                startActivityForResult(intent, STASHION_SIGN_IN_REQUEST);
                //startActivityForResult(intent, DETAILED_STASHION_REQUEST);
            }
        });

        //SHOULD I BE GETTING CHILD? - no, not in MainActivity
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();


                while (iterator.hasNext()) {
                    final Stashion stashion = iterator.next().getValue(Stashion.class);

                    stashionsList.add(stashion);
                    stashionAdapter.notifyDataSetChanged();
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
            if (requestCode == CREATE_STASHION_REQUEST) {
                Bundle b = data.getExtras();
                Stashion stashion = b.getParcelable("stashion");

                stashionsList.add(stashion);
                stashionAdapter.notifyDataSetChanged();
            }

            else if(requestCode==STASHION_SIGN_IN_REQUEST){
                String username = data.getStringExtra("username");
                Intent intent = new Intent(MainActivity.this, DetailedStashionActivity.class);
                intent.putExtra("username", username);
                Bundle b = data.getExtras();
                Stashion mStashion = b.getParcelable("stashion");

                intent.putExtra("stashion", mStashion);
                startActivityForResult(intent, DETAILED_STASHION_REQUEST);
            }
            //no special actions for detailed_stashion_request
        }

    }


    public void addStashion(View v) {
        Intent createStashionIntent = new Intent(this, CreateStashionActivity.class);
        startActivityForResult(createStashionIntent, CREATE_STASHION_REQUEST);
    }
}
