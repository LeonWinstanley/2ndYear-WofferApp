package com.example.wofferapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    };
    public int colorThemeApp = 1;
    String currUserID = getFirebaseUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Integer> compOff = new ArrayList<Integer>();
    public UserDetails currUser = new UserDetails(currUserID, 0 , compOff, colorThemeApp);

    public void setCurrentUser(UserDetails us){
        currUser = us;
    }

    public void syncCurrentUser() {
        DocumentReference docIdRef = db.collection("users").document(currUserID);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currUser = document.toObject(UserDetails.class);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DocumentReference docIdRef = db.collection("users").document(currUserID);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currUser = document.toObject(UserDetails.class);
                    } else {
                        docIdRef.set(currUser);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });

        // Initialize the bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_offers);

        // Using RxPermissions library create a new object
        RxPermissions rxPermissions = new RxPermissions(this);
        // Request for location perms, fine and course at the same time
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        // Do nothing yet if granted
                    } else {
                        // This means at least one permission has not been granted
                    }
                });


        //Here we set up the application by initializing the fragment to be a profile fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new OffersFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    // Switch statement to replace the currently being viewed fragment with a new
                    // fragment dependant on which icon is pressed
                    switch (menuItem.getItemId()){
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.navigation_offers:
                            selectedFragment = new OffersFragment();
                            //((OffersFragment) selectedFragment).setCurrentUser(currUser);
                            break;
                        case R.id.navigation_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    public void goToLogin (View view){
        Intent intent = new Intent (this, LoginActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(intent);
    }
}


