package com.example.wofferapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ProfileFragment extends Fragment {

    public UserDetails currentUser = new UserDetails();





    public void setCurrentUser(UserDetails us){
        currentUser = us;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {





    FirebaseFirestore db = FirebaseFirestore.getInstance();
        setCurrentUser(((MainActivity) getActivity()).currUser);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView currentOffer = ((TextView) v.findViewById(R.id.profileText));

        TextView offerDescription = ((TextView) v.findViewById(R.id.profileCurrentOffer));

        TextView offerCode1 = ((TextView) v.findViewById(R.id.profileReward1));
        TextView offerCode2 = ((TextView) v.findViewById(R.id.profileReward2));
        TextView offerCode3 = ((TextView) v.findViewById(R.id.profileReward3));

        ImageView offerImg = ((ImageView) v.findViewById(R.id.image));






        db.collection("offers")
                .whereEqualTo("id", currentUser.getCurrentOfferid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                OfferDetails offer = document.toObject(OfferDetails.class);
                                currentOffer.setText(offer.getTitle());


                                offerDescription.setText((offer.getDescription()));


                                //offerCode1.setText();
                                //offerCode2.setText();
                                //offerCode3.setText();
                                // needs to search through user completed array then show offer ID



                                offerImg.setImageBitmap(getImageBitmap(offer.getImg()));


                            }
                        }
                    }
                });

        List<Integer> compOffers = currentUser.getCompletedOffers();
        for(int i = 0; i < compOffers.size(); i++)
        {
            Integer offID = compOffers.get(i);
            if(i == 0)
            {
                db.collection("offers")
                        .whereEqualTo("id", offID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        OfferDetails offer = document.toObject(OfferDetails.class);

                                        offerCode1.setText(offer.getReward());

                        }}}});
            }
            else if (i == 1)
            {
                db.collection("offers")
                        .whereEqualTo("id", offID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        OfferDetails offer = document.toObject(OfferDetails.class);

                                        offerCode2.setText(offer.getReward());

                                    }}}});
            }
            else if(i == 2)
            {
                db.collection("offers")
                        .whereEqualTo("id", offID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        OfferDetails offer = document.toObject(OfferDetails.class);

                                        offerCode3.setText(offer.getReward());

                                    }}}});
            }
            else
            {
                break;
            }

        }



        return v;

    }




    public FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
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

}



