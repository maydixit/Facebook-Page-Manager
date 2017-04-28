package com.example.may.facebook_page_manager;

/**
 * Created by May on 4/1/17.
 */

public class PageInfo {
    String page_name, page_id, page_access_token;
    public PageInfo(String page_name, String page_id, String page_access_token){
        this.page_access_token = page_access_token;
        this.page_id = page_id;
        this.page_name = page_name;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }

    public String getPage_id() {
        return page_id;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }

    public String getPage_access_token() {
        return page_access_token;
    }

    public void setPage_access_token(String page_access_token) {
        this.page_access_token = page_access_token;
    }
}
