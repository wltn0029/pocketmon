package com.example.viewpagerdemo;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Output;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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

public class GoogleLoginActivity extends Activity {
    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    final StringBuilder sb = new StringBuilder();
    BufferedReader bfreader=null;
    InputStreamReader reader=null;
    InputStream is;
    URL url;
    {
        try {
            url = new URL("http://143.248.140.251:9380/account/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    HttpURLConnection connection =null;
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
        if (account != null) {
            int id=0;
            try{
                String str = loadID();
                JSONObject json = new JSONObject(str);
                id = (int) json.get("pk");
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.d("id:",String.valueOf(id));
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userAccount", account.toString());
            Log.d("GOOGLE_LOGIN>>>>>", account.toString());
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
                intent.putExtra("userAccount", "");
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
            int id=0;
            try{
                String str = loadID();
                JSONObject json = new JSONObject(str);
                id = (int) json.get("pk");
            }catch(Exception e){
                e.printStackTrace();
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userAccount", account.toString());
            Log.d("GOOGLE_LOGIN>>>>>", account.toString());
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
        try{
            //POST : google login account
            Thread sendHttpRequestThread = new Thread() {
                @Override
                public void run(){
                    //Initialize variables
                    //HttpURLConnection httpConnection = null;
                    try {
                        // Set URL to get all contacts
                        //URL url = new URL(url);
                        // Open HttpURLConnection of the URL and set method & timeout
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        connection.setConnectTimeout(1000);

                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("account",account.toString());
                        InputStream in = new BufferedInputStream(connection.getInputStream());

                        writer.write(jsonObject.toString());
                        writer.flush();
                        writer.close();
                        os.close();

                        connection.connect();
                        Log.d("ADDCONTACT>>>>>", "Request to post " + account.toString());
                        Log.d("ADDCONTACT>>>>>", "Response:" + connection.getResponseMessage());
                    } catch (MalformedURLException e) {
                        Log.d("ADDCONTACT>>>>>", "malformed url exception");
                    } catch (IOException e) {
                        Log.d("ADDCONTACT>>>>>", "IOException");
                    } catch (JSONException e){
                        Log.d("ADDCONTACT>>>>>", "JSONExection");
                    } finally{
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            };
            sendHttpRequestThread.start();
            //GET : personal id
          /*  connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            is = connection.getInputStream();
            reader = new InputStreamReader(is);
            bfreader = new BufferedReader(reader);
            String line = bfreader.readLine();
            StringBuffer readTextBuf = new StringBuffer();
            while(line!=null){
                readTextBuf.append(line);
                sb.append(line);
                line = bfreader.readLine();
            }*/
        } catch(Exception e){
            e.printStackTrace();
        }/*finally {
            try{
                if(bfreader!=null){
                    bfreader.close();
                    bfreader = null;
                }
                if(reader!=null){
                   reader.close();
                   reader = null;
                }
                if(connection !=null){
                    connection.disconnect();
                    connection=null;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }*/
        return sb.toString();
    }
}