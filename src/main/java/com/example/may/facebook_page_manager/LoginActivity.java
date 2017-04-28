package com.example.may.facebook_page_manager;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;

import java.util.Arrays;

/*
Initial Activity class.
Logs you in to facebook with the required permissions.
 */
public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    Activity currentActivity ;
    boolean logged_in = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        currentActivity = this;
        if (AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
        }


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPublishPermissions(Arrays.asList("publish_pages", "manage_pages" ));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Success tag", loginResult.toString());
                if (! logged_in) {
                    logged_in = true;
                    LoginManager.getInstance().logInWithReadPermissions(currentActivity, Arrays.asList("read_insights"));

                }
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    Log.d("user response", response.getJSONObject().toString());
                                    Intent intent = new Intent(getApplicationContext(), PageListingActivity.class);
                                    intent.putExtra("user_name", response.getJSONObject().get("name").toString());
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                ).executeAsync();


            }

            @Override
            public void onCancel() {
                Log.d("Cancel tag", "cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ex tag", error.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy(){
        LoginManager.getInstance().logOut();
        super.onDestroy();
    }

}

