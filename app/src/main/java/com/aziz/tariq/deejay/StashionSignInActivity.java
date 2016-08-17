package com.aziz.tariq.deejay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class StashionSignInActivity extends AppCompatActivity {

    MenuItem mNextButton;
    int mPasscode;
    Stashion mStashion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stashion_sign_in);

        Bundle b = getIntent().getExtras();
        mStashion = b.getParcelable("stashion");
        mPasscode = mStashion.getPasscode();

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
                stashionSignIn();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void stashionSignIn() {
        EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
        EditText stashionPasscodeEditText = (EditText)findViewById(R.id.stashion_passcode_edit_text);

        String userName = userNameEditText.getText().toString();
        String stashionPasscodeString = stashionPasscodeEditText.getText().toString();
        int stashionPasscode = Integer.parseInt(stashionPasscodeString);

        if(userName.equals("") || stashionPasscodeString.equals("")){
            Toast.makeText(this, "Fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(stashionPasscode!=mPasscode){
            Toast.makeText(this, "Incorrect passcode!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            User user = new User(userName);

            Intent intent = new Intent();
            intent.putExtra("stashion", mStashion);
            intent.putExtra("username", userName);
            //intent.putExtra("user", user);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


}
