package com.example.may.facebook_page_manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by May on 3/30/17.
 * Activity to list all pages
 */

public class PageListingActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    ArrayList<String> page_names;
    ArrayList<JSONObject> pages;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_listing);
        username = getIntent().getExtras().getString("user_name");
        ((TextView)findViewById(R.id.login_info_pagelister)).setText("Logged in as: " + username);

        final ListView listView = (ListView) findViewById(R.id.page_list);
        page_names = new ArrayList<String>();
        pages = new ArrayList<JSONObject>();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, page_names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                //Go to a new activity where the page can be managed.
                Intent intent = new Intent(getApplicationContext(), PageManagementActivity.class);
                intent.putExtra("page_name", page_names.get(position));
                try {
                    intent.putExtra("user_name", username);
                    intent.putExtra("page_token", String.valueOf(pages.get(position).get("access_token")));
                    intent.putExtra("page_id", String.valueOf(pages.get(position).get("id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/accounts",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            Log.d("result: ", response.toString());
                            // Data contains the list of pages
                        try {

                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            for (int i = 0 ; i < data.length(); i++){
                                String pagename = String.valueOf(data.getJSONObject(i).get("name"));
                                Log.d("page", pagename);
                                page_names.add(pagename);
                                pages.add(data.getJSONObject(i));
                                adapter.notifyDataSetChanged();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
