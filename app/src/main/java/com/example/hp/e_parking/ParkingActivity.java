package com.example.hp.e_parking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.example.hp.e_parking.models.Parking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class ParkingActivity extends AppCompatActivity {

    //Button btn1, btn2, btn3, btn4, btn5, btn18;
    Random random = new Random();
    GridLayout gridLayout;

    SimpleDateFormat currentMonth = new SimpleDateFormat("dd/MM/yyyy");
    Date todayMonth = new Date();
    String thisDate = currentMonth.format(todayMonth);

    String name, vehicle, email;
    int lastSpot = 0;
    int lastUnparkedSpot = 0;

    private DatabaseReference mFirebaseParkingDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(ParkingActivity.this, LoginActivity2.class));
                    finish();
                } else {
                    //Toast.makeText(ParkingActivity.this, "User not null!!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        setContentView(R.layout.activity_main);
        gridLayout = (GridLayout) findViewById(R.id.Gridlayout);


        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseParkingDatabase = mFirebaseInstance.getReference("parking");


        name = Vault.getSharedPreferencesString(this, "name", "invalid name");
        email = Vault.getSharedPreferencesString(this, "email", "invalid email");
        vehicle = Vault.getSharedPreferencesString(this, "vehicleNo", "invalid vehicle");
        loadLastPosition();

    }


    public void getAllParkingObjects() {
        mFirebaseInstance.getReference().child("parking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                procressAllParkings((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled", "Failed to load getAllObjects.", databaseError.toException());
            }
        });
    }

    private void procressAllParkings(Map<String, Object> value) {
        if (value != null) {
            for (String key : value.keySet()) {
                try {
                    Parking parking = new Gson().fromJson(new Gson().toJson(value.get(key)), Parking.class);
                    if (parking != null) {
                        View button = gridLayout.getChildAt(parking.getPosition() - 1);
                        if (button != null) {
                            if (parking.isParked) {
                                button.setBackgroundColor(Color.RED);
                            } else {
                                button.setBackgroundColor(Color.GREEN);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        //Toast.makeText(this, "Logout clicked ...", Toast.LENGTH_SHORT).show();


        auth.signOut();


    }

    private void loadLastPosition() {

//        // store app title to 'app_title' node
//        mFirebaseInstance.getReference("lastSpot").setValue(lastSpot);


        // app_title change listener
        mFirebaseInstance.getReference("lastSpot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    lastSpot = dataSnapshot.getValue(Integer.class);
                } else {
                    lastSpot = 21;
                }


                Log.e("onDataChange", "lastSpot  updated : " + lastSpot);


                setButtonColors2(gridLayout);
                setButtonColors(gridLayout);
                getAllParkingObjects();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.e("onCancelled", "Failed to update lastSpot.", error.toException());
            }
        });
    }

    public void check(final Button btn, final String key, final int position) {
        getParkingDetailsForPosition(position, btn);
    }

    private void getParkingDetailsForPosition(final int position, final Button btn) {
        mFirebaseParkingDatabase.child("POS_" + position).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Parking parking = dataSnapshot.getValue(Parking.class);
                    handleParking(parking, btn, position);
                } else {
                    handleParking(null, btn, position);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", databaseError.getMessage());
            }
        });
    }


    private void handleParking(final Parking parking, final Button btn, final int position) {
        String message = null;

        if (parking != null&&parking.isParked) {


                if (parking.getEmail().toLowerCase().trim().equals(email.trim().trim())) {
                    message = parking.getMessage();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle("Do you want to unpark?");
                    builder.setMessage(message);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            btn.setBackgroundColor(Color.GREEN);
                            Toast.makeText(getApplicationContext(), "Successfully UnParked", Toast.LENGTH_LONG).show();
                            Vault.putSharedPreferencesString(ParkingActivity.this, "name", null);
                            Vault.putSharedPreferencesString(ParkingActivity.this, "vehicleNo", null);
                            Vault.putSharedPreferencesString(ParkingActivity.this, "email", null);
                            updateParkingSpotonServer(parking.position, null, false);

                        }
                    })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //btn.setBackgroundColor(Color.GREEN);
                                    Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.create().show();
                } else {
                    Toast.makeText(ParkingActivity.this, getDate(parking.startTime, "dd/MM/yyyy hh:mm:ss.SSS") + " =>Not your parking", Toast.LENGTH_SHORT).show();
                }


        } else {

            final StringBuilder result = new StringBuilder();
            String tittle = null;
            final int token = random.nextInt(900000) + 123456;

            tittle = "Do you want to park here?";
            result.append("\nToken no: #" + String.valueOf(token));
            result.append("\nName: " + name);
            result.append("\nVehicle no: " + vehicle);
            result.append("\nBlock no: " + btn.getText());
            result.append("\nDATE: " + thisDate);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(tittle);
            builder.setMessage(result.toString());
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    btn.setBackgroundColor(Color.RED);

                    mFirebaseInstance.getReference("lastSpot").setValue(21);
                    Toast.makeText(getApplicationContext(), "Successfully Parked", Toast.LENGTH_LONG).show();
                    updateParkingSpotonServer(position, result.toString(), true);

                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //btn.setBackgroundColor(Color.GREEN);
                            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
                        }
                    });
            builder.create().show();

        }
    }

    private void updateParkingSpotonServer(int position, String message, boolean isParked) {
        mFirebaseParkingDatabase.child("POS_" + position).setValue(new Parking(position, message, isParked, email, System.currentTimeMillis()));
        getAllParkingObjects();
    }

    public void btnClick(View v) {
        int id = v.getId();
        String btnName = getResources().getResourceEntryName(id);
        String[] str = btnName.split("_");
        int btnPosition = Integer.valueOf(str[1]);
        //Log.d("button",""+btnPosition);
        check((Button) v, btnName, btnPosition);
    }

    public void setButtonColors(GridLayout gridLayout) {

        int count = gridLayout.getChildCount();
        Log.d("childCount", "" + count);
        for (int i = count; i > lastSpot; i--) {
            View v = gridLayout.getChildAt(i);
            if (v instanceof Button) {
                v.setEnabled(false);

            }
        }

    }

    public void setButtonColors2(GridLayout gridLayout) {

        int count = gridLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = gridLayout.getChildAt(i);
            if (v instanceof Button) {
                v.setEnabled(true);
                String token = Vault.getSharedPreferencesString(this, getResources().getResourceEntryName(v.getId()), null);
                if (token != null) {
                    v.setBackgroundColor(Color.RED);
                } else {
                    v.setBackgroundColor(Color.GREEN);
                }
            }
        }

    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
