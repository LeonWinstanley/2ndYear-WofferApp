package com.example.wofferapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class OffersFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener
        , OnMapReadyCallback {

    // Create a map object
    GoogleMap mMap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public UserDetails currentUser = new UserDetails();
    OfferDetails currentOffer;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;

    public OffersFragment() {
        // Empty Constructor
    }

    public void setCurrentUser(UserDetails us){
        currentUser = us;
    }

    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    };

    // When the view is first started, inflate it to the container size
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        View v = inflater.inflate(R.layout.fragment_offers, container, false);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
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
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Once the map object had been fetched, set the map object up by adding a marker,
    // Then try and setMyLocation to true, catch the security exception if the perms have not been
    // enabled. Possibly ask for them again? Ignore for now
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(setMarkerWindow());
        LatLng trent = new LatLng(52.9117779,-1.1854268);
        LatLng nottingham = new LatLng(52.9493, -1.1471);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100); // two minute interval
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        try{mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                Looper.myLooper());}
        catch (SecurityException e){
            // No Location Perms
        }

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

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                mLastLocation = location;
                db.collection("offers")
                        .whereEqualTo("id", currentUser.getCurrentOfferid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        currentOffer = document.toObject(OfferDetails.class);
                                        if(location.getLatitude() < currentOffer.getPosition().getLatitude()+0.0001f &&
                                                location.getLatitude() > currentOffer.getPosition().getLatitude()-0.0001f &&
                                                location.getLongitude() < currentOffer.getPosition().getLongitude()+0.0001f &&
                                                location.getLatitude() > currentOffer.getPosition().getLatitude()-0.0001f){
                                            Toast.makeText(getContext(), "OFFER GOTTT", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    //Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });


            }
        }
    };

    @Override
    public void onInfoWindowClick(Marker marker) {
        OfferDetails markerTag = (OfferDetails) marker.getTag();
        currentUser.setCurrentOfferid(markerTag.getID());
        ((MainActivity)getActivity()).currUser = currentUser;
        DocumentReference usersRef = db.collection("users")
                .document(getFirebaseUser().getUid());
        usersRef
                .update("currentOfferid", markerTag.getID())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Offer Added!", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error Adding Offer", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
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
