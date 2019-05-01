package com.example.wofferapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OffersFragment extends Fragment implements OnMapReadyCallback {

    // Create a map object
    GoogleMap mMap;
    public OffersFragment() {
        // Empty Constructor
    }

    // When the view is first started, inflate it to the container size
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_offers, container, false);
        return v;

    }

    // Once onCreateView has returned a view, get the map object asynchronously once the
    // map object is ready
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Once the map object had been fetched, set the map object up by adding a marker,
    // Then try and setMyLocation to true, catch the security exception if the perms have not been
    // enabled. Possibly ask for them again? Ignore for now
    @Override
    public void onMapReady(GoogleMap googleMap) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mMap = googleMap;
        LatLng trent = new LatLng(52.9117779,-1.1854268);
        mMap.addMarker(new MarkerOptions().position(trent).title("Marker in Nottingham"));

        db.collection("offers")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                OfferDetails offer = document.toObject(OfferDetails.class);
                                double latitude = offer.getPosition().getLatitude();
                                double longitude = offer.getPosition().getLongitude();
                                LatLng location = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(location).title(offer.getTitle()));
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        DocumentReference docRef = db.collection("offers").document("10%off");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            // No location services, maybe a dialog here?
        }

        // Finally, move the camera to the marker and set the zoom to 15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(trent, 15.0f));

    }

}
