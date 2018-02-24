package com.kshitijpatil.roboconanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 111;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build()))
                            .setIsSmartLockEnabled(false, true)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            //Successfully signed in
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar("Sign in cancelled");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar("No internet connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar("Unknown error");
                    return;
                }
            }
            showSnackbar("Unknown Sign-in response");
        }
    }

    void showSnackbar(String message) {
        snackbar = Snackbar.make(findViewById(R.id.main_view), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
