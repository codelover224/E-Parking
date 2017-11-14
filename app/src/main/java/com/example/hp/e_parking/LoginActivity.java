package com.example.hp.e_parking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by hp on 02-Sep-17.
 */

public class LoginActivity extends AppCompatActivity {

    Button signup;
    EditText edit1, edit2, edit3;
    String abc, xyz, pqr;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        signup = (Button) findViewById(R.id.sign_up_btn);
        edit1 = (EditText) findViewById(R.id.eText_name);
        edit2 = (EditText) findViewById(R.id.eText_vehicleNo);
        edit3 = (EditText) findViewById(R.id.email);



//        mFirebaseInstance = FirebaseDatabase.getInstance();
//
//        // get reference to 'users' node
//        mFirebaseDatabase = mFirebaseInstance.getReference("users");
    }

    public void btnClick1(View v) {
        abc = edit1.getText().toString();
        xyz = edit2.getText().toString();
        pqr = edit3.getText().toString();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        int len = abc.length();
        int len2 = xyz.length();

        if (len == 0 || len2 == 0) {
            Toast.makeText(this, "Please Enter all information", Toast.LENGTH_LONG).show();
        } else if (!pqr.matches(emailPattern)) {
            Toast.makeText(this, "Please Enter proper email-id", Toast.LENGTH_LONG).show();
        } else {
            Vault.putSharedPreferencesString(LoginActivity.this, "name",  abc);
            Vault.putSharedPreferencesString(LoginActivity.this, "vehicleNo",  xyz);
            Vault.putSharedPreferencesString(LoginActivity.this, "email",  pqr);
            Intent i = new Intent(this, ParkingActivity.class);

            startActivity(i);
        }
    }






}
