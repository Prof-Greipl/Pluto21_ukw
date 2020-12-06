package de.hawlandshut.pluto21_ukw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener{

    EditText mPostTitle;
    EditText mPostBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPostTitle = (EditText) findViewById( R.id.postTitle);
        mPostBody  = (EditText) findViewById( R.id.postText);

        //  TODO: Remove; test setting
        mPostBody.setText("Lore ipsum se...");
        mPostTitle.setText("Title");

        ((Button) findViewById( R.id.postButtonPost)).setOnClickListener( this );

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.postButtonPost:
                doPost();
                return;
            default:
                return;
        }
    }

    private void doPost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("uid", user.getUid());
        postMap.put("author", user.getEmail());
        postMap.put("title", mPostTitle.getText().toString());
        postMap.put("body", mPostBody.getText().toString());
        postMap.put("timestamp", ServerValue.TIMESTAMP);

        DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference("Posts/");
        mDatabase.push().setValue(postMap);
    }

}