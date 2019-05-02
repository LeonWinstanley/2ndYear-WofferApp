package com.example.wofferapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class OffersFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

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
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(setMarkerWindow());

        LatLng trent = new LatLng(52.9117779,-1.1854268);
        LatLng nottingham = new LatLng(52.9493, -1.1471);
        //mMap.addMarker(new MarkerOptions().position(trent).title("Marker in Nottingham"));

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

                                Marker newMark = mMap.addMarker(new MarkerOptions()
                                        .position(location));

                                newMark.setTag(offer);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "Info window clicked",
                Toast.LENGTH_SHORT).show();
        OfferDetails markerTag = (OfferDetails) marker.getTag();

    }

    private GoogleMap.InfoWindowAdapter setMarkerWindow() {
        return new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View myContentView = getLayoutInflater().inflate(
                        R.layout.info_marker, null);

                OfferDetails markerTag = (OfferDetails) marker.getTag();

                //TextView offerTitle = ((TextView) myContentView
                        //.findViewById(R.id.title));

                //offerTitle.setText(markerTag.getTitle());

                //TextView offerDesc = ((TextView) myContentView
                       //.findViewById(R.id.description));

                //offerDesc.setText(markerTag.getDescription());

                ImageView offerImg = ((ImageView) myContentView
                        .findViewById(R.id.image));

                offerImg.setImageBitmap(getImageBitmap(markerTag.getImg()));

                return myContentView;
            }

            private Bitmap getImageBitmap(String url) {
                Bitmap bm = null;
                try {
                    URL aURL = new URL(url);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    //Log.e(TAG, "Error getting bitmap", e);
                }
                return bm;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
    }

}
