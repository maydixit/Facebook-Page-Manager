package com.example.may.facebook_page_manager;

import android.util.Log;

import java.util.Date;

/**
 * Created by May on 4/1/17.
 * List item for the custom list adapter
 */

public class ListItem {
    private String message_chars;
    private String time;
    private String id;

    //private int viewed_by;

    public ListItem(String message, String time_created, String id){
        message_chars = message;
        time = parse_time(time_created);
        this.id = id;
    }

    private String parse_time(String date) {
        String temp = date.substring(0, 10);
        temp += " " + date.substring(12, 16);
        return temp;
    }

    public String getMessage_chars() {
        return message_chars;
    }

    public void setMessage_chars(String message_chars) {
        this.message_chars = message_chars;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
