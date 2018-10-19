package com.vocabulary.realm;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.realm.Realm;

public abstract class RealmFragment extends Fragment {

    protected RealmActivity mActivity;
    protected Realm mRealm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (RealmActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = mActivity.getRealm();
    }
}
