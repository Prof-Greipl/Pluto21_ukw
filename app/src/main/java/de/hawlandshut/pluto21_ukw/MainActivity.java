package de.hawlandshut.pluto21_ukw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hawlandshut.pluto21_ukw.model.Post;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "xx MainActivity";

    // TODO: Just for testing; reomve later
    private boolean mIsSignedIn = false;
    private static final String TEST_USERNAME = "Dieter Greipl";
    private static final String TEST_MAIL = "dieter.greipl@gmail.com";
    private static final String TEST_PASSWORD = "123456";

    // The place to store posts received from server
    ArrayList<Post> mPostList = new ArrayList<Post>();

    // Adapter beetween ListView and our list of posts
    ArrayAdapter<Post> mAdapter;

    // ChildeEventLister
    ChildEventListener mChildEventListener;
    Query mQuery;

    // UI Element deklarieren
    ListView mListView;

    boolean mListenerIsRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

       mAdapter = new ArrayAdapter<Post>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                mPostList
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Log.d(TAG, "getView called with position " +  position);
                View view = super.getView(position, convertView, parent);

                TextView text1, text2;
                text1 = (TextView) view.findViewById(android.R.id.text1);
                text2 = (TextView) view.findViewById(android.R.id.text2);

                Post p = getItem(position);
                text1.setText(p.title);
                text2.setText(p.body);

                return view;
            }


        };

        // Adapter mit der Listview verbinden
        mListView = (ListView) findViewById(R.id.listViewMessages);
        mListView.setAdapter(mAdapter);

        // ChildEventListener initialisieren
        mChildEventListener = getChildEventListener();
        mQuery = FirebaseDatabase.getInstance().getReference().child("Posts/").limitToLast( 3 );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            resetApp();
            Intent intent = new Intent( getApplication(), SignInActivity.class);
            startActivity( intent );
        } else {
            if (!mListenerIsRunning){
                mPostList.clear();
                mQuery.addChildEventListener( mChildEventListener );
                mListenerIsRunning = true;
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    void resetApp(){
        if (mListenerIsRunning){
            mQuery.removeEventListener( mChildEventListener );
            mListenerIsRunning = false;
        }
        mPostList.clear();
        mAdapter.notifyDataSetChanged();
    }

    private ChildEventListener getChildEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG,"CEL: onChildAdded Key = " + dataSnapshot.getKey());
                Post p = Post.fromSnapShot( dataSnapshot);
                mPostList.add( p );
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Log.d(TAG,"CEL: onChildChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,"CEL: onChildRemoved Key = " + dataSnapshot.getKey());
                String key = dataSnapshot.getKey();

                for (int i = 0; i < mPostList.size(); i++) {
                    if (key.equals(mPostList.get(i).firebaseKey)) {
                        mPostList.remove(i);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG,"CEL: onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"CEL: onCancelled");
                mListenerIsRunning = false;
            }
        };
    }

    /* Erzeugen des Menus aus der XML-Datei */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /* Reaktion auf Clicks in die Menue-Einträge */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // TODO: Clean up!
        Intent intent;
        switch (item.getItemId()) {
            case R.id.mainMenuGotoManageAccount:
                intent = new Intent( getApplication(), ManageAccountActivity.class);
                startActivity( intent );
                return true;

            case R.id.mainMenuGotoPost:
                intent = new Intent( getApplication(), PostActivity.class);
                startActivity( intent );
                return true;

            case R.id.mainMenuTest:
                // TODO: Remover later
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

   // TODO: Consider removing..
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}