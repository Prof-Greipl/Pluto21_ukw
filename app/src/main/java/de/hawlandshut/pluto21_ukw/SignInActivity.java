package de.hawlandshut.pluto21_ukw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "xx SignInActivity";

    // Deklaration der UI-Elemente
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    Button mButtonSignIn;
    Button mButtonCreateAccount;
    Button mButtonResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "onCreate called");
        // Initialisieren der UI Elemente
        mEditTextEmail = (EditText) findViewById( R.id.signInEmail);
        mEditTextPassword = (EditText) findViewById( R.id.signInPassword);
        mButtonSignIn = (Button)  findViewById( (R.id.signInButtonSignIn));
        mButtonCreateAccount = (Button)  findViewById( (R.id.signInButtonCreateAccount));
        mButtonResetPassword = (Button)  findViewById( (R.id.signInButtonResetPassword));

        // Listener setzen
        mButtonSignIn.setOnClickListener( this );
        mButtonResetPassword.setOnClickListener( this );
        mButtonCreateAccount.setOnClickListener( this );

        //TODO: Nur zum Testen  - remove later
        mEditTextEmail.setText("dietergreipl@gmail.com");
        mEditTextPassword.setText("123456");

    }


    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");

        // Hole den aktuellen User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
           finish();
        }

    }
    @Override
    public void onClick( View v){
        switch( v.getId() ){

            case R.id.signInButtonCreateAccount:
                doCreateAccount();
                return;

            case R.id.signInButtonResetPassword:
                doResetPassword();
                return;

            case R.id.signInButtonSignIn:
                doSignIn();
                return;

            default:
                return;
        }
    }

    private void doSignIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();

        // TODO: Übung
        // Prüfen: Ist das Password Feld leer? Mindestlänge? Ist die Eingage für email gültig?
        if (user == null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword( email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User signed in.", Toast.LENGTH_LONG).show();
                                finish();
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

    private void doResetPassword() {
        Toast.makeText(getApplicationContext(), "You pressed Reset Password (nyi).", Toast.LENGTH_LONG).show();
    }

    private void doCreateAccount() {
        Intent intent = new Intent( getApplication(), CreateAccountActivity.class);
        startActivity( intent );
    }

}