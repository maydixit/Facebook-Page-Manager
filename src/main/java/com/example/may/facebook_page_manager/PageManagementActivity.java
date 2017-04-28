package com.example.may.facebook_page_manager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.GONE;
/*
Activity to manage pages: make, edit, delete posts etc. All work is done here
 */
public class PageManagementActivity extends AppCompatActivity {

    private static final int PUBLISHED = 1, UNPUBLISHED = 0, SCHEDULED = 2;
    private int MAX_MESSAGE_LENGTH = 20;
    int overlay_layout_id; //Note : this mechanism is for one overlay visible at a time.
    PageInfo page;
    CustomListAdapter unpub_adapter, pub_adapter;
    AccessToken PageAT;
    Context currentContext;
    boolean newViewsAdded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_management);
        currentContext = this;
        initialize_content();
        set_list_adapters();
        set_up_listeners();
    }


    private void initialize_content() {
        overlay_layout_id = 0;
        page = new PageInfo( getIntent().getExtras().getString("page_name") , getIntent().getExtras().getString("page_id"), getIntent().getExtras().getString("page_token"));
        String username = getIntent().getExtras().getString("user_name");
        ((TextView)findViewById(R.id.login_info_text_postlist)).setText("Logged in as: " + username);
        ((TextView)findViewById(R.id.header_page_manager)).setText(page.getPage_name());
        PageAT = new AccessToken(page.getPage_access_token(), AccessToken.getCurrentAccessToken().getApplicationId(), AccessToken.getCurrentAccessToken().getUserId(), AccessToken.getCurrentAccessToken().getPermissions(), null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, AccessToken.getCurrentAccessToken().getExpires(), null);
    }

    private void set_list_adapters() {
        //populate the lists unpub posts and pub posts here Set up adapters
        ListView unpubListview = (ListView) findViewById(R.id.unpublished_list);
        ListView pubListview = (ListView) findViewById(R.id.published_list);

        registerForContextMenu(unpubListview);
        registerForContextMenu(pubListview);


        unpub_adapter = new CustomListAdapter(this, new ArrayList<ListItem>());
        pub_adapter = new CustomListAdapter(this, new ArrayList<ListItem>());

        unpubListview.setAdapter(unpub_adapter);
        pubListview.setAdapter(pub_adapter);
    }

    private void set_up_listeners() {
        Button post_button = (Button) findViewById(R.id.post_button);
        Button make_pub_post_button = (Button) findViewById(R.id.make_post_pub_button_frag);
        Button make_unpub_post_button = (Button) findViewById(R.id.make_post_unpub_button_frag);
        Button make_sch_post_button = (Button) findViewById(R.id.publish_with_date_button);

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.post_button).setVisibility(View.GONE);
                findViewById(R.id.posting_fragment_linear).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.input_post_frag)).setText("Say something");
                overlay_layout_id = R.id.posting_fragment_linear;
            }
        });

        make_pub_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_post_request(PageAT, set_post_params(PUBLISHED));
            }
        });

        make_unpub_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_post_request(PageAT, set_post_params(UNPUBLISHED));
            }
        });

        make_sch_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_post_request(PageAT, set_post_params(SCHEDULED));
            }
        });

    }

    void update_content(){
        pub_adapter.clear();
        unpub_adapter.clear();
        make_graph_request(PageAT, "/"+page.getPage_id()+"/feed", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Log.d("posts", response.toString());
                add_content(response, PUBLISHED);
            }
        } );

        make_graph_request(PageAT,  "/"+page.getPage_id()+"/promotable_posts", set_fetch_params(UNPUBLISHED), HttpMethod.GET, new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("posts", response.toString());
                        add_content(response, UNPUBLISHED);
                    }
                });
    }

    private void add_content(GraphResponse response, int type) {
        // TODO: Paging. add options to fetch more posts
        // TODO: only displays messages of the post, add support to display media content

        switch (type) {
            case PUBLISHED:

                try {
                    JSONArray data = response.getJSONObject().getJSONArray("data");
                    for (int i = 0 ; i < data.length(); i++) {
                        String message = String.valueOf(data.getJSONObject(i).get("message"));
                        if ( message.length() > MAX_MESSAGE_LENGTH) message = message.substring(0, MAX_MESSAGE_LENGTH) + "...";
                        String time = String.valueOf(data.getJSONObject(i).get("created_time"));
                        String id = String.valueOf(data.getJSONObject(i).get("id"));
                        pub_adapter.add(new ListItem(message, time, id));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case UNPUBLISHED:
                try {
                    JSONArray data = response.getJSONObject().getJSONArray("data");
                    for (int i = 0 ; i < data.length(); i++){
                        String message = String.valueOf(data.getJSONObject(i).get("message"));
                        if ( message.length() > MAX_MESSAGE_LENGTH) message = message.substring(0, MAX_MESSAGE_LENGTH) + "...";
                        String time = String.valueOf(data.getJSONObject(i).get("created_time"));
                        String id = String.valueOf(data.getJSONObject(i).get("id"));
                        unpub_adapter.add(new ListItem(message, time, id));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.published_list) {
            String[] menuItems = getResources().getStringArray(R.array.published_options);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(PUBLISHED, i, i, menuItems[i]);
            }
        }
        else if (v.getId()==R.id.unpublished_list){
            String[] menuItems = getResources().getStringArray(R.array.unpublished_options);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(UNPUBLISHED, i, i, menuItems[i]);
            }
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems;
        String menuItemName;

        switch(item.getGroupId()){
            case PUBLISHED:
                menuItems = getResources().getStringArray(R.array.published_options);
                menuItemName = menuItems[menuItemIndex];
                do_action(menuItemName, info.position, PUBLISHED);
                break;
            case UNPUBLISHED:
                menuItems = getResources().getStringArray(R.array.unpublished_options);
                menuItemName = menuItems[menuItemIndex];
                do_action(menuItemName, info.position, UNPUBLISHED);
                break;
        }

        return true;
    }

    private void do_action(String menuItemName, int position, int group_id) {
        CustomListAdapter adapter;
        if (group_id == PUBLISHED) adapter = pub_adapter;
        else adapter = unpub_adapter;

        switch(menuItemName){
            case "View Content":
                findViewById(R.id.post_button).setVisibility(View.GONE);
                findViewById(R.id.view_content_linear_layout).setVisibility(View.VISIBLE);
                overlay_layout_id = R.id.view_content_linear_layout;

                make_graph_request(PageAT, "/"+adapter.getItem(position).getId(), null, HttpMethod.GET, new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                Log.d("post content: ", response.toString());
                                String message = response.getJSONObject().getString("message");
                                ViewManipulator.add_view_to_linear_layout((LinearLayout)
                                        findViewById(R.id.view_content_linear_layout),
                                        ViewManipulator.create_text_view(message, currentContext, 24, View.TEXT_ALIGNMENT_CENTER));
                                newViewsAdded = true;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } );
                break;
            case "View Details":
                findViewById(R.id.post_button).setVisibility(View.GONE);
                findViewById(R.id.view_content_linear_layout).setVisibility(View.VISIBLE);
                overlay_layout_id = R.id.view_content_linear_layout;
                make_graph_request(PageAT, "/"+adapter.getItem(position).getId()+"/insights/post_impressions_unique", null, HttpMethod.GET, new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            Log.d("post details: ", response.toString());
                        try {
                            String value = String.valueOf(response.getJSONObject().getJSONArray("data").getJSONObject(0).getJSONArray("values").getJSONObject(0).get("value"));
                            String title = String.valueOf(response.getJSONObject().getJSONArray("data").getJSONObject(0).get("title"));
                            ViewManipulator.add_view_to_linear_layout((LinearLayout)findViewById(R.id.view_content_linear_layout), ViewManipulator.create_text_view(title + " : " + value, currentContext, 24, View.TEXT_ALIGNMENT_CENTER));
                            newViewsAdded = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } );

                break;
            case "Publish":

                break;
        }
    }

    private Bundle set_post_params(int type) {
        Bundle params = new Bundle();
        String text = ((EditText)findViewById(R.id.input_post_frag)).getText().toString();
        params.putString("message", text);
        switch (type){
            case PUBLISHED:
                break;

            case UNPUBLISHED:
                params.putString("published", "false");
                break;
            case SCHEDULED:
                String time = ((TextView)findViewById(R.id.date_input)).getText().toString();
                String epoch = get_epoch_time(time);
                params.putString("scheduled_publish_time", epoch);
                params.putString("published", "false");

        }
        return params;
    }

    // Input format: 2011-07-19T18:23:20+0000
    //output format: epoch
    private String get_epoch_time(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date gmt = null;
        try {
            gmt = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisecondsSinceEpoch0 = gmt.getTime();
        String asString = formatter.format(gmt);
        Log.d("epoc", String.valueOf(millisecondsSinceEpoch0));
        String e = String.valueOf(millisecondsSinceEpoch0);
        return e.substring(0, e.length() - 3);
    }

    private Bundle set_fetch_params(int type){
        Bundle params = new Bundle();
        String text = ((EditText)findViewById(R.id.input_post_frag)).getText().toString();
        params.putString("message", text);
        switch (type){
            case PUBLISHED:
                break;

            case UNPUBLISHED:
                params.putString("is_published", "false");
                break;
        }
        return params;
    }

    void make_post_request(AccessToken pageAT, Bundle params){
        new GraphRequest(
                pageAT,
                "/"+page.getPage_id() +"/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                                /* handle the result */
                        Log.d("POSTED", response.toString());
                        close_overlay();
                    }
                }
        ).executeAsync();
    }

    void make_graph_request(AccessToken at, String path,  Bundle params, HttpMethod method, GraphRequest.Callback callback){
        new GraphRequest(at, path, params, method, callback).executeAsync();
    }


    @Override
    protected void onResume(){
        super.onResume();
        update_content();
        Log.d("place", "onResume");
    }

    @Override
    public void onBackPressed(){
        if (overlay_layout_id != 0) close_overlay();
        else super.onBackPressed();
    }

    void close_overlay(){
        LinearLayout linearLayout = (LinearLayout) findViewById(overlay_layout_id);
        if (newViewsAdded && linearLayout.getChildCount() > 0) {
                newViewsAdded = false;
                linearLayout.removeAllViews();
        }
        linearLayout.setVisibility(GONE);
        findViewById(R.id.post_button).setVisibility(View.VISIBLE);
        overlay_layout_id = 0;
        update_content();
    }

}
