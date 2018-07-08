package com.example.kebelzsolt.dictionary20.main.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.example.kebelzsolt.dictionary20.R;

/**
 * Created by Kébel Zsolt on 2018. 03. 24..
 */

public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView facebookLink = (TextView) findViewById(R.id.tv_facebook_link);
        facebookLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
