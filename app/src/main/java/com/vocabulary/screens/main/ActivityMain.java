package com.vocabulary.screens.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.Subject;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.RealmFragment;
import com.vocabulary.realm.Vocabulary;

import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class ActivityMain extends RealmActivity {
    public static final int NO_ACTION = 0;
    public static final int VOCABULARY_DELETED = 1;
    public static final int VOCABULARY_RENAMED = 2;
    public static final int VOCABULARY_MERGED = 3;

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_VOCABULARIES = 1;
    public static final int FRAGMENT_ADD = 2;
    public static final int FRAGMENT_SETTINGS = 3;

    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.fl_fragment_frame) FrameLayout mFrameLayout;

    private RealmResults<Vocabulary> mVocabularies = null;
    private Vocabulary mChangedVocabulary;

    private ProgressDialog mProgressDialog;

    private RealmFragment[] mFragments = new RealmFragment[]{
            new FragmentHome(),
            new FragmentVocabularies(),
            new FragmentAddVocabulary(),
            new FragmentSettings()
    };

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);
        ButterKnife.bind(this);

        setupTabs();

        displayFragment(getFragment(FRAGMENT_VOCABULARIES));

        //initialize the languages
        ArrayList<Subject> subjects = JSONParser.getSubjects(this);
        for (int i = 0; i < subjects.size(); i++) {
            final Vocabulary vocabulary = new Vocabulary();
            vocabulary.set(subjects.get(i).getSubject(), "");

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.copyToRealmOrUpdate(vocabulary);
                }
            });
        }

        mVocabularies = mRealm.where(Vocabulary.class).sort(Vocabulary.SORT_STRING).findAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case VOCABULARY_DELETED:
                createUndoSnackbar();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        showProgressDialog(false);
    }

    @Override
    public void onBackPressed() {
        if (mTabLayout.getSelectedTabPosition() != 1)
            mTabLayout.getTabAt(1).select();
        else
            super.onBackPressed();
    }

    //private methods
    private View setupTab(@NonNull View customTabView, int icon, Integer title, int color, int typeface) {
        ImageView ivIcon = customTabView.findViewById(R.id.iv_icon);
        TextView tvText = customTabView.findViewById(R.id.tv_text);

        ivIcon.setImageDrawable(getDrawable(icon));
        tvText.setTextColor(getResources().getColor(color));
        tvText.setTypeface(null, typeface);
        if (title != null) tvText.setText(getString(title));

        return customTabView;
    }

    private void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());

        View customTabView1 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        mTabLayout.getTabAt(0).setCustomView(
                setupTab(customTabView1, R.drawable.home, R.string.tab_home, R.color.black, Typeface.NORMAL)
        );

        View customTabView2 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        mTabLayout.getTabAt(1).setCustomView(
                setupTab(customTabView2, R.drawable.book_sel, R.string.tab_vocabularies, R.color.page2, Typeface.NORMAL)
        );

        View customTabView3 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        mTabLayout.getTabAt(2).setCustomView(
                setupTab(customTabView3, R.drawable.new_add, R.string.tab_add, R.color.black, Typeface.NORMAL)
        );

        View customTabView4 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        mTabLayout.getTabAt(3).setCustomView(
                setupTab(customTabView4, R.drawable.new_settings2, R.string.tab_settings, R.color.black, Typeface.NORMAL)
        );

        mTabLayout.getTabAt(1).select();

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mTabLayout.getTabAt(0).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.home_sel, null, R.color.page2, Typeface.BOLD)
                        );
                        break;
                    case 1:
                        mTabLayout.getTabAt(1).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.book_sel, null, R.color.page2, Typeface.BOLD)
                        );
                        break;
                    case 2:
                        mTabLayout.getTabAt(2).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.add_sel, null, R.color.page2, Typeface.BOLD)
                        );
                        break;
                    case 3:
                        mTabLayout.getTabAt(3).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.new_settings_selected, null, R.color.page2, Typeface.BOLD)
                        );
                        break;
                }
                displayFragment(mFragments[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mTabLayout.getTabAt(0).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.home, null, R.color.black, Typeface.NORMAL)
                        );
                        break;
                    case 1:
                        mTabLayout.getTabAt(1).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.book, null, R.color.black, Typeface.NORMAL)
                        );
                        break;
                    case 2:
                        mTabLayout.getTabAt(2).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.new_add, null, R.color.black, Typeface.NORMAL)
                        );
                        break;
                    case 3:
                        mTabLayout.getTabAt(3).setCustomView(
                                setupTab(tab.getCustomView(), R.drawable.new_settings2, null, R.color.black, Typeface.NORMAL)
                        );
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void createUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                R.string.message_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRealm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                realm.copyToRealmOrUpdate(mChangedVocabulary);
                            }
                        });
                    }
                });
        snackbar.show();
    }

    private void displayFragment(@NonNull Fragment fragment) {
        fragment.setEnterTransition(new Fade());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mFrameLayout.getId(), fragment);
        transaction.commit();
    }

    //public methods
    public void setSelectedVocabulary(final Vocabulary vocabulary) {
        mRealm.beginTransaction();
        mChangedVocabulary = mRealm.copyFromRealm(vocabulary);
        mRealm.commitTransaction();

        vocabulary.removeAllChangeListeners();
        vocabulary.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
            @Override
            public void onChange(@NonNull RealmModel realmModel, @Nullable ObjectChangeSet changeSet) {
                assert changeSet != null;
                if (!changeSet.isDeleted())
                    mChangedVocabulary = mRealm.copyFromRealm(vocabulary);
            }
        });
    }

    public void showProgressDialog(boolean show) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(getResources().getString(R.string.just_a_moment));
            mProgressDialog.setCancelable(false);
        }

        if (show)
            mProgressDialog.show();
        else
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public Fragment getFragment(int index) {
        return mFragments[index];
    }

    public RealmResults<Vocabulary> getVocabularies()
    {
        return mVocabularies;
    }

    public Vocabulary getSelectedVocabulary() {
        return mChangedVocabulary;
    }
}