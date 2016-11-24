package com.cs442.team4.tahelper.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.cs442.team4.tahelper.model.TokenEntity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by neo on 12-11-2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private DatabaseReference mDatabase;

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");//Displaying token on logcat

        SharedPreferences.Editor editor = getSharedPreferences("TAHelper", MODE_PRIVATE).edit();
        editor.clear();
        editor.putString("refreshedToken", refreshedToken);
        editor.commit();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setId(token);
        mDatabase.child("token").child(token).setValue(tokenEntity);

        //You can implement this method to store the token on your server
        //Not required for current project
    }
}
