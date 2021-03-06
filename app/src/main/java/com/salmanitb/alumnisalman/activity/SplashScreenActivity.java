package com.salmanitb.alumnisalman.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.salmanitb.alumnisalman.R;
import com.salmanitb.alumnisalman.SalmanApplication;
import com.salmanitb.alumnisalman.helper.APIConnector;
import com.salmanitb.alumnisalman.helper.PreferenceManager;
import com.salmanitb.alumnisalman.helper.WebService;
import com.salmanitb.alumnisalman.model.UserAuth;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Context context = this;
        UserAuth userAuth = SalmanApplication.getCurrentUserAuth();
        Gson gson = new Gson();
        Log.d("SALMAN_APP splash", gson.toJson(userAuth));
        if (userAuth != null) {

            // Handle unverified user
            if (!userAuth.isVerified()) {
                gotoConfirmation();
                return;
            }

            Log.d("USER_TOKEN", userAuth.getToken());

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    gotoMain();
                }
            }, 500);

        } else { // no cached user data found
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    gotoLogin();
                }
            }, 500);
        }

    }

    private void gotoLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void gotoConfirmation() {
        Intent i = new Intent(this, ConfirmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void gotoMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
