package com.vocabulary.realm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.vocabulary.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmActivity extends AppCompatActivity {

    private RealmConfiguration configuration;
    protected Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mRealm = Realm.getDefaultInstance();
        } catch (Exception e) {
            //Must call once in the application
            Realm.init(this);
            //TODO Realm schema
            configuration = new RealmConfiguration.Builder()
                    .schemaVersion(2)
                    .migration(new Migration())
                    .build();
            Realm.setDefaultConfiguration(configuration);
            mRealm = Realm.getDefaultInstance();
            mRealm.close();
        }

        setWindowColors(getWindowColor());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRealm.close();
    }

    private void setWindowColors(Integer color) {
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
    }

    protected int getWindowColor() {
        return getResources().getColor(R.color.page2_dark);
    }
}
