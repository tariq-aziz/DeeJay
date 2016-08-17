package com.aziz.tariq.deejay;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;

public class CreateStashionActivity extends AppCompatActivity {

    private MenuItem mNextButton;

    private EditText stashionNameEditText;
    private EditText stashionPasscodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stashion);

        getSupportActionBar().setTitle("Create a Stashion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        initializeLayout();
    }

    private void initializeLayout() {
        stashionNameEditText = (EditText) findViewById(R.id.stashion_name_edit_text);
        stashionPasscodeEditText = (EditText) findViewById(R.id.stashion_passcode_edit_text);
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
                createAccount();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void createAccount(){
        String stashionName = stashionNameEditText.getText().toString();
        String stashionPassCodeString = stashionPasscodeEditText.getText().toString();
        int stashionPasscode = Integer.parseInt(stashionPassCodeString);

        if(!stashionName.equals("") && !stashionPassCodeString.equals("")){
            Stashion stashion = new Stashion(stashionName, stashionPasscode);
            Intent intent = new Intent();
            intent.putExtra("stashion", stashion);
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            Toast.makeText(CreateStashionActivity.this, "Fill out all fields!", Toast.LENGTH_SHORT).show();
        }
    }

}
