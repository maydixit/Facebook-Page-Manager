package com.example.may.facebook_page_manager;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by May on 4/2/17.
 * Activity to manipulate views. Can hold static methods to create dynamic content. To be used for displaying media etc.
 */

public class ViewManipulator {
    static int PADDING = 5;

    static void add_view_to_linear_layout(LinearLayout layout, View view ){

        view.setPadding(PADDING, PADDING, PADDING, PADDING);
        view.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(view);
    }

    static TextView create_text_view(String message, Context context, int size, int alignment){
        TextView tv = new TextView(context);
        tv.setText(message);
        tv.setTextSize(size);
        tv.setTextAlignment(alignment);
        return tv;
    }

}
