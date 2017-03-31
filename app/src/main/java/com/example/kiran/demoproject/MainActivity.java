package com.example.kiran.demoproject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kiran.demoproject.Utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private SignInButton btSignIn;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInOptions();
        initViews();
    }

    private void googleSignInOptions() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void initViews() {
        btSignIn = (SignInButton) findViewById(R.id.main_googlesign);
        btSignIn.setScopes(gso.getScopeArray());
        btSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_googlesign:
//                connectivityCheck();
                if (connectivityCheck() != null) {
                    dialogLoading();
                    Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(this, "No NetWorkConnection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dialogLoading() {
        progressDialog = Utils.dialogLoading(MainActivity.this, getString(R.string.dialog_mainactivity_load));
        progressDialog.show();
    }

    private NetworkInfo connectivityCheck() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:ConnectionResult " + connectionResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (googleSignInResult.isSuccess()) {
            progressDialog.dismiss();
            startActivity(new Intent(MainActivity.this, ListDataActivity.class));
        }
    }
}
