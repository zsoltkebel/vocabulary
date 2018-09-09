package com.vocabulary.realm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.vocabulary.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

abstract public class RealmActivity extends AppCompatActivity {

    private RealmConfiguration configuration;
    protected Realm mRealm;

    abstract protected Activity getActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && getActivity() != null) {
            startActivity(getActivity().getIntent());
            getActivity().finish();
        }

        try {
            mRealm = Realm.getDefaultInstance();
        } catch (Exception e) {
            //Must call once in the application
            Realm.init(this);

            configuration = new RealmConfiguration.Builder()
                    .schemaVersion(3)
                    .migration(new Migration())
                    .build();

            Realm.setDefaultConfiguration(configuration);
            mRealm = Realm.getDefaultInstance();
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
        //window.setNavigationBarColor(color);
    }

    protected int getWindowColor() {
        return getResources().getColor(R.color.page2_dark);
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void hideKeyboard(View view) {
        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
