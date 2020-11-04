package de.hawlandshut.pluto21_ukw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEditTextEMail;
    private EditText mEditTextPassword1;
    private EditText mEditTextPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEditTextEMail =  ((EditText) findViewById(R.id.createAccountEmail));
        mEditTextPassword1 =  ((EditText) findViewById(R.id.createAccountPassword1));
        mEditTextPassword2 =  ((EditText) findViewById(R.id.createAccountPassword2));

        // TODO: Remove presets later
        mEditTextEMail.setText("xxx@gmail.com");
        mEditTextPassword1.setText("123456");
        mEditTextPassword2.setText("123456");

        // Add Listener
        ((Button) findViewById(R.id.createAccountButtonCreateAccount)).setOnClickListener( this );

    }

    @Override
    public void onClick( View v){
        switch( v.getId() ){

            case R.id.createAccountButtonCreateAccount:
                doCreateAccount();
                return;

            default:
                return;
        }
    }

    private void doCreateAccount() {
        Toast.makeText(getApplicationContext(), "You pressed Create Account (nyi).", Toast.LENGTH_LONG).show();
    }
}