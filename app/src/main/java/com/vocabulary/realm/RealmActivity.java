package com.vocabulary.realm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;

public class RealmActivity extends AppCompatActivity {

    protected Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mRealm = Realm.getDefaultInstance();
        } catch (Exception e) {
            Realm.init(this);
            mRealm = Realm.getDefaultInstance();
        }
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
}
