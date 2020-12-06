package de.hawlandshut.pluto21_ukw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "xx ManageActivity";

    TextView mEmail, mAccountState, mTechnicalId;
    EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        mEmail = (TextView) findViewById(R.id.manageAccountEmail);
        mAccountState = (TextView) findViewById(R.id.manageAccountVerificationState);
        mTechnicalId = (TextView) findViewById(R.id.manageAccountTechnicalId);
        mPassword = (EditText) findViewById(R.id.manageAccountPassword);

        ((Button) findViewById( R.id.manageAccountButtonSignOut)).setOnClickListener( this );
        ((Button) findViewById( R.id.manageAccountButtonSendActivationMail)).setOnClickListener( this );
        ((Button) findViewById( R.id.manageAccountButtonDeleteAccount)).setOnClickListener( this );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mEmail.setText("Your E-Mail : " + user.getEmail());
        mAccountState.setText("Verified : " + user.isEmailVerified());
        mTechnicalId.setText("UID : " + user.getUid());

        // Disable SendActivationMail Button, if account is verified
        if (user.isEmailVerified())
            ((Button) findViewById( R.id.manageAccountButtonSendActivationMail)).setVisibility( View.GONE );

    }

    @Override
    public void onClick( View v){
        switch( v.getId() ){

            case R.id.manageAccountButtonDeleteAccount:
                doDeleteAccount();
                return;

            case R.id.manageAccountButtonSendActivationMail:
                doSendActivationMail();
                return;

            case R.id.manageAccountButtonSignOut:
                doSignOut();
                return;

            default:
                return;
        }
    }

    private void doSignOut() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "No user signed in.",
                    Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "You are signed out.",
                    Toast.LENGTH_SHORT).show();
            finish();
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

    private void doDeleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "User deleted.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "User deletion failed :" + task.getException(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Sign In Fehler " + task.getException());
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "You are not signed in. Pls sign in and delete then.", Toast.LENGTH_LONG).show();
        }

    }
}