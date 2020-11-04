package de.hawlandshut.pluto21_ukw;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import java.util.ArrayList;

import de.hawlandshut.pluto21_ukw.model.Post;
import de.hawlandshut.pluto21_ukw.test.PostTestData;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "xx MainActivity";

    // TODO: Just for testing; reomve later
    private boolean mIsSignedIn = false;

    // The place to store posts received from server
    ArrayList<Post>  mPostList = new ArrayList<Post>();

    // Adapter beetween ListView and our list of posts
    ArrayAdapter<Post> mAdapter;

    // UI Element deklarieren
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);

        // TODO: Erzeugen und Setzen der Testdaten - remove in prod. version
        PostTestData.createTestData();
        mPostList = (ArrayList<Post>) PostTestData.postTestList;

        mAdapter = new ArrayAdapter<Post>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                mPostList
        ){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view =  super.getView(position, convertView, parent);

                TextView text1, text2;
                text1 = (TextView) view.findViewById( android.R.id.text1);
                text2 = (TextView) view.findViewById( android.R.id.text2);

                text1.setText("Zeile 1");
                text2.setText("Zeile 2");

                return view;
            }
        };

        // Adapter mit der Listview verbinden
        mListView =(ListView) findViewById( R.id.listViewMessages );
        mListView.setAdapter( mAdapter );
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
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
        switch( item.getItemId()) {
            case R.id.mainMenuHelp:
                Toast.makeText(getApplicationContext(), "You pressed HELP.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Menue Help erkannt");
                return true;

            case R.id.mainMenuManageAccount:
                Toast.makeText(getApplicationContext(), "You pressed MANAGE ACC.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Menue Manage Account erkannt");
                return true;

            case R.id.mainMenuGotoSignIn:
                intent = new Intent(getApplication(),
                        SignInActivity.class);
                startActivity(intent);
                return true;

            case R.id.mainMenuGotoCreateAccount:
                intent = new Intent(getApplication(),
                        CreateAccountActivity.class);
                startActivity(intent);
                return true;

            case R.id.mainMenuGotoManageAccount:
                intent = new Intent(getApplication(),
                        ManageAccountActivity.class);
                startActivity(intent);
                return true;

            case R.id.mainMenuGotoPost:
                intent = new Intent(getApplication(),
                        PostActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
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