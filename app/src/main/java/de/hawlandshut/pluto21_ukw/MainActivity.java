package de.hawlandshut.pluto21_ukw;

import android.content.Intent;
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
import de.hawlandshut.pluto21_ukw.test.PostTestData;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);

        // TODO: Erzeugen und Setzen der Testdaten - remove in prod. version
        //PostTestData.createTestData();
        //mPostList = (ArrayList<Post>) PostTestData.postTestList;

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
        mChildEventListener = new ChildEventListener() {
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
                mPostList.remove(0);
                mAdapter.notifyDataSetChanged();
                // TODO: Schleife korrekt programmieren.
                /* for (int i = 0; i < mPostList.size(); i++){
                    if( key.equals( mPostList.get(i).firebaseKey)){
                        mPostList.remove(i);
                        break;
                    }
                }*/

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG,"CEL: onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"CEL: onCancelled");
            }
        };
        mQuery = FirebaseDatabase.getInstance().getReference().child("Posts/").limitToLast( 3 );
        mQuery.addChildEventListener( mChildEventListener );
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");


        // Hole den aktuellen User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            // Kein auth. User...
            Log.d(TAG,"on Start: No user, therefore go to SignInActivity");
            Intent intent = new Intent( getApplication(), SignInActivity.class);
            startActivity( intent );
        }

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

            case R.id.mainMenuTestWrite:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("uid", user.getUid());
                postMap.put("author", user.getEmail());
                postMap.put("title", "My Title");
                postMap.put("body", "Body");
                postMap.put("timestamp", ServerValue.TIMESTAMP);

                DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference("Posts/");
                mDatabase.push().setValue(postMap);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doSendActivationMail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Mail sent.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sending mail failed :" + task.getException(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Send mail fehler " + task.getException());
                        }
                    }
                });
    }

    private void doResetPassword() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(TEST_MAIL)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Mail sent.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sending mail failed :" + task.getException(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Send reset password Fehler " + task.getException());
                        }
                    }
                });

    }

    private void doDelete() {
        // Hole user-Objekt
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "User deleted.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "User deletion failed :" + task.getException(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Sign In Fehler " + task.getException());
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "You are not signed in. Pls sign in and delete then.", Toast.LENGTH_LONG).show();
        }
    }

    private void doSignOut() {
        // Hole user-Objekt
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "No user signed in.",
                    Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "You are signed out.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void doSignIn() {
        // Hole user-Objekt
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(TEST_MAIL, TEST_PASSWORD)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User signed in.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Sign-in Failed :" + task.getException(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Sign In Fehler " + task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You are already signed in. Sign out first.", Toast.LENGTH_LONG).show();
        }
    }

    private void doCreateTestUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(TEST_MAIL, TEST_PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Created User", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "User Creation Failed :" + task.getException(), Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Create Account Fehler " + task.getException());
                        }
                    }
                });
    }


    private void doTestAuthentication() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            // User is signed in
            Toast.makeText(getApplicationContext(), "Signed In:" + mUser.getEmail() + " Act :" + mUser.isEmailVerified(), Toast.LENGTH_LONG).show();
        } else {
            // No user is signed in
            Toast.makeText(getApplicationContext(), "Not Signed In", Toast.LENGTH_LONG).show();
        }

    }

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