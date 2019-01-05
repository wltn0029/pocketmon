package com.example.viewpagerdemo;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Output;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleLoginActivity extends Activity {
    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    InputStream is;
    public static String responseStr;
    int id;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        boolean firstVisited = false;
        if (account != null) {
            try{
                String reply =loadID();
                JsonParser parser = new JsonParser();
                Log.d("asdfasdf",1+"");
                JsonElement element = parser.parse(reply);
                Log.d("asdfasdf",2+"");
                id = element.getAsJsonObject().get("pk").getAsInt();
                Log.d(">>>>>>>>>>>>>","responseStr : "+reply);

                Log.d(">>>>>>>>>>>>>>>",String.valueOf(id),null);
                firstVisited = element.getAsJsonObject().get("is_first").getAsBoolean();
            }catch(Exception e){
                e.printStackTrace();
            }
            //Log.d("id:",String.valueOf(id));
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userAccountID", String.valueOf(id));
            intent.putExtra("isFirstVisited",firstVisited);
            Log.d(">>>>>>>>>>>>>>>", "id:"+id+"firstvisited"+String.valueOf(firstVisited));
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button signInButton = findViewById(R.id.signInBtn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        Button continueButton = findViewById(R.id.continueBtn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userAccountID", "");
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, pass account
            boolean firstVisited=false;
            try{
                String reply=loadID();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(reply);
                Log.d(">>>>>>>>>>>>>","responseStr : "+reply);
                id = element.getAsJsonObject().get("pk").getAsInt();
                firstVisited = element.getAsJsonObject().get("is_first").getAsBoolean();
            }catch(Exception e){
                e.printStackTrace();
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userAccountID", String.valueOf(id));
            intent.putExtra("isFirstVisited",firstVisited);
            Log.d(">>>>>>>>>>>>>>>", "id:"+id+"firstvisited"+String.valueOf(firstVisited));
            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE LOGIN>>>>>", "signInResult:failed code=" + e.getStatusCode());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userAccount", "");
            startActivity(intent);
        }
    }

    public String loadID(){
        String reply;
        Call<ResponseBody> call = RetrofitClient
                                    .getInstacne()
                                    .getApi()
                                    .useraccount(account.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    responseStr = response.body().string();
                    Log.d(">>>>>>>>>>>>>","3 : "+responseStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GoogleLoginActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
            }
        });
        reply = responseStr;
        return reply;
    }
}