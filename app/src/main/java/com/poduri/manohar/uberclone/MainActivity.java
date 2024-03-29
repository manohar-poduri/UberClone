package com.poduri.manohar.uberclone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    @Override
    public void onClick(View view) {

        if (edtDriverOrPassenger.getText().toString().equals("Driver") || edtDriverOrPassenger.getText().toString().equals("Passenger")){
            if (ParseUser.getCurrentUser()== null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null && e == null){
                            Toast.makeText(MainActivity.this, "We have an anonymus User!!", Toast.LENGTH_LONG).show();

                            user.put("as", edtDriverOrPassenger.getText().toString());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    transationToPassengerActivity();
                                    transationToDriverActivity();
                                }
                            });
                        }
                    }
                });
            }
        }


    }
    enum State {
        SIGNUP, LOGIN
    }
    private State state;
    private Button btnSignUp, btnOneTimeLogin;
    private EditText edtUsername,edtPassword, edtDriverOrPassenger;
    private RadioButton PassengerRadioButton, DriverRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (ParseUser.getCurrentUser() != null){

          //  ParseUser.logOut();
            transationToPassengerActivity();
            transationToDriverActivity();

        }
        btnSignUp = findViewById(R.id.btnSignUp);
        DriverRadioButton = findViewById(R.id.rbdDriver);
        PassengerRadioButton = findViewById(R.id.rbdPassenger);
        btnOneTimeLogin = findViewById(R.id.btnOneTImeLogin);
        btnOneTimeLogin.setOnClickListener(this);


        state = State.SIGNUP;

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtDriverOrPassenger = findViewById(R.id.edtDOrP);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state == State.SIGNUP) {
                    if (DriverRadioButton.isChecked() == false && PassengerRadioButton.isChecked() == false) {
                        Toast.makeText(MainActivity.this, "Are you a Driver Or Passenger", Toast.LENGTH_LONG).show();
                        return;

                    }
                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());
                    if (DriverRadioButton.isChecked()){
                        appUser.put("as", "Driver");
                    } else if (PassengerRadioButton.isChecked()) {
                        appUser.put("as", "Passenger");
                    }

                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(MainActivity.this,"Signed Up!!",Toast.LENGTH_LONG).show();
                                transationToPassengerActivity();
                                transationToDriverActivity();
                            }
                        }
                    });
                } else if (state == State.LOGIN) {
                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {


                            if (user != null && e == null) {
                                Toast.makeText(MainActivity.this,"User Logged In!!",Toast.LENGTH_LONG).show();
                                transationToPassengerActivity();
                                transationToDriverActivity();
                            }
                        }
                    });
                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.LoginItem:
                if (state == State.SIGNUP) {
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                    btnSignUp.setText("Log In");
                    
                } else if (state == State.LOGIN) {
                    state = State.SIGNUP;
                    item.setTitle("Log In");
                    btnSignUp.setText("Sign Up");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void transationToPassengerActivity(){

        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().get("as").equals("Passenger")){

                Intent intent = new Intent(MainActivity.this,PassengerActivity.class);
                startActivity(intent);
            }
        }

    }
    private void transationToDriverActivity(){

        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().get("as").equals("Driver")){

                Intent intent = new Intent(this, DriverActivity.class);
                startActivity(intent);
            }
        }
    }
}

