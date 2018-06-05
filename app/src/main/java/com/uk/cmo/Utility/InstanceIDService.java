package com.uk.cmo.Utility;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by usman on 29-05-2018.
 */

public class InstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "InstanceIDService";

    public InstanceIDService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken= FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG,refreshedToken);

        sendRegistrationTokenToServer(refreshedToken);

    }

    private void sendRegistrationTokenToServer(String token){

        Log.d(TAG,token);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null) {
            reference.child(Constants.USERS).child(user.getUid()).child(Constants.USERS_TOKEN).setValue(token);
        }
    }
}
