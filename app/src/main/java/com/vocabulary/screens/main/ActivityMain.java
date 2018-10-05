package com.vocabulary.screens.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.more.ActivityMore;

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
    public static final int VOCABULARY_DELETED = 1;
    public static final int VOCABULARY_RENAMED = 2;
    public static final int VOCABULARY_MERGED = 3;
    public static final int NO_ACTION = 0;

    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    protected ViewPager mViewPager;

    @BindView(R.id.fl_fragment_frame)
    protected FrameLayout frameLayout;

    private RealmResults<Vocabulary> mVocabularies = null;
    private Vocabulary mSelectedVocabulary;

    private Fragment[] fragments = new Fragment[]{
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

        displayFragment(fragments[1]);




//TODO for tests set to true if mRealm object parameters changed
        if (false) {
            Realm.deleteRealm(Realm.getDefaultConfiguration());
            mRealm = Realm.getDefaultInstance();
        }
        if (false)
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });


        //initialize the languages
        ArrayList<Subject> subjects = JSONParser.getSubjects(this);
        for (int i = 0; i < subjects.size(); i++) {
            final Vocabulary vocabulary = new Vocabulary();
            vocabulary.set(subjects.get(i).getSubject(), "");

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
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
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getResources().getString(R.string.message_deleted), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mRealm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(mSelectedVocabulary);
                                    }
                                });
                            }
                        });
                snackbar.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getFragmentVocabularyList().getProgressDialog().isShowing())
            getFragmentVocabularyList().getProgressDialog().cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getFragmentVocabularyList().getProgressDialog().isShowing())
            getFragmentVocabularyList().getProgressDialog().cancel();
    }

    @Override
    public void onBackPressed() {
        if (mTabLayout.getSelectedTabPosition() != 1)
            mTabLayout.getTabAt(1).select();
        else
            super.onBackPressed();
    }

    public void startActivityMore(String vocabularyId) {
        Intent intent = new Intent(this, ActivityMore.class);
        intent.putExtra(Vocabulary.ID, vocabularyId);
        startActivityForResult(intent, NO_ACTION);
    }

    public void setupTabs() {
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());

        View customTabView1 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ImageView ivIcon1 = (ImageView) customTabView1.findViewById(R.id.iv_icon);
        TextView tvText1 = (TextView) customTabView1.findViewById(R.id.tv_text);

        ivIcon1.setImageDrawable(getDrawable(R.drawable.home));
        tvText1.setText(getResources().getString(R.string.tab_home));
        tvText1.setTextColor(getResources().getColor(R.color.black));
        tvText1.setTypeface(tvText1.getTypeface(), Typeface.NORMAL);
        mTabLayout.getTabAt(0).setCustomView(customTabView1);

        View customTabView2 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ImageView ivIcon2 = (ImageView) customTabView2.findViewById(R.id.iv_icon);
        TextView tvText2 = (TextView) customTabView2.findViewById(R.id.tv_text);

        ivIcon2.setImageDrawable(getDrawable(R.drawable.book_sel));
        tvText2.setText(getResources().getString(R.string.tab_vocabularies));
        tvText2.setTextColor(getResources().getColor(R.color.page2));
        tvText2.setTypeface(tvText2.getTypeface(), Typeface.BOLD);
        mTabLayout.getTabAt(1).setCustomView(customTabView2);

        View customTabView3 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ImageView ivIcon3 = (ImageView) customTabView3.findViewById(R.id.iv_icon);
        TextView tvText3 = (TextView) customTabView3.findViewById(R.id.tv_text);

        ivIcon3.setImageDrawable(getDrawable(R.drawable.new_add));
        tvText3.setText(getResources().getString(R.string.tab_add));
        tvText3.setTextColor(getResources().getColor(R.color.black));
        tvText3.setTypeface(tvText3.getTypeface(), Typeface.NORMAL);
        mTabLayout.getTabAt(2).setCustomView(customTabView3);

        View customTabView4 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ImageView ivIcon4 = (ImageView) customTabView4.findViewById(R.id.iv_icon);
        TextView tvText4 = (TextView) customTabView4.findViewById(R.id.tv_text);

        ivIcon4.setImageDrawable(getDrawable(R.drawable.new_settings2));
        tvText4.setText(getResources().getString(R.string.tab_settings));
        tvText4.setTextColor(getResources().getColor(R.color.black));
        tvText4.setTypeface(tvText3.getTypeface(), Typeface.NORMAL);
        mTabLayout.getTabAt(3).setCustomView(customTabView4);

        mTabLayout.getTabAt(1).select();

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        View customTabView1 = tab.getCustomView();
                        ImageView ivIcon1 = (ImageView) customTabView1.findViewById(R.id.iv_icon);
                        TextView tvText1 = (TextView) customTabView1.findViewById(R.id.tv_text);

                        ivIcon1.setImageDrawable(getDrawable(R.drawable.home_sel));
                        tvText1.setTextColor(getResources().getColor(R.color.page2));
                        tvText1.setTypeface(tvText1.getTypeface(), Typeface.BOLD);
                        mTabLayout.getTabAt(0).setCustomView(customTabView1);
                        break;
                    case 1:
                        View customTabView2 = tab.getCustomView();
                        ImageView ivIcon2 = (ImageView) customTabView2.findViewById(R.id.iv_icon);
                        TextView tvText2 = (TextView) customTabView2.findViewById(R.id.tv_text);

                        ivIcon2.setImageDrawable(getDrawable(R.drawable.book_sel));
                        tvText2.setTextColor(getResources().getColor(R.color.page2));
                        tvText2.setTypeface(tvText2.getTypeface(), Typeface.BOLD);
                        mTabLayout.getTabAt(1).setCustomView(customTabView2);
                        break;
                    case 2:
                        View customTabView3 = tab.getCustomView();
                        ImageView ivIcon3 = (ImageView) customTabView3.findViewById(R.id.iv_icon);
                        TextView tvText3 = (TextView) customTabView3.findViewById(R.id.tv_text);

                        ivIcon3.setImageDrawable(getDrawable(R.drawable.add_sel));
                        tvText3.setTextColor(getResources().getColor(R.color.page2));
                        tvText3.setTypeface(tvText3.getTypeface(), Typeface.BOLD);
                        mTabLayout.getTabAt(2).setCustomView(customTabView3);
                        break;
                    case 3:
                        View customTabView4 = tab.getCustomView();
                        ImageView ivIcon4 = (ImageView) customTabView4.findViewById(R.id.iv_icon);
                        TextView tvText4 = (TextView) customTabView4.findViewById(R.id.tv_text);

                        ivIcon4.setImageDrawable(getDrawable(R.drawable.new_settings_selected));
                        tvText4.setTextColor(getResources().getColor(R.color.colorAccent));
                        tvText4.setTypeface(tvText4.getTypeface(), Typeface.BOLD);
                        mTabLayout.getTabAt(3).setCustomView(customTabView4);
                        break;
                }
                displayFragment(fragments[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        View customTabView1 = tab.getCustomView();
                        ImageView ivIcon1 = (ImageView) customTabView1.findViewById(R.id.iv_icon);
                        TextView tvText1 = (TextView) customTabView1.findViewById(R.id.tv_text);

                        ivIcon1.setImageDrawable(getDrawable(R.drawable.home));
                        tvText1.setTextColor(getResources().getColor(R.color.black));
                        tvText1.setTypeface(null, Typeface.NORMAL);
                        mTabLayout.getTabAt(0).setCustomView(customTabView1);
                        break;
                    case 1:
                        View customTabView2 = tab.getCustomView();
                        ImageView ivIcon2 = (ImageView) customTabView2.findViewById(R.id.iv_icon);
                        TextView tvText2 = (TextView) customTabView2.findViewById(R.id.tv_text);

                        ivIcon2.setImageDrawable(getDrawable(R.drawable.book));
                        tvText2.setTextColor(getResources().getColor(R.color.black));
                        tvText2.setTypeface(null, Typeface.NORMAL);
                        mTabLayout.getTabAt(1).setCustomView(customTabView2);
                        break;
                    case 2:
                        View customTabView3 = tab.getCustomView();
                        ImageView ivIcon3 = (ImageView) customTabView3.findViewById(R.id.iv_icon);
                        TextView tvText3 = (TextView) customTabView3.findViewById(R.id.tv_text);

                        ivIcon3.setImageDrawable(getDrawable(R.drawable.new_add));
                        tvText3.setTextColor(getResources().getColor(R.color.black));
                        tvText3.setTypeface(null, Typeface.NORMAL);
                        mTabLayout.getTabAt(2).setCustomView(customTabView3);
                        break;
                    case 3:
                        View customTabView4 = tab.getCustomView();
                        ImageView ivIcon4 = (ImageView) customTabView4.findViewById(R.id.iv_icon);
                        TextView tvText4 = (TextView) customTabView4.findViewById(R.id.tv_text);

                        ivIcon4.setImageDrawable(getDrawable(R.drawable.new_settings2));
                        tvText4.setTextColor(getResources().getColor(R.color.black));
                        tvText4.setTypeface(null, Typeface.NORMAL);
                        mTabLayout.getTabAt(3).setCustomView(customTabView4);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public FragmentVocabularies getFragmentVocabularyList() {
        return (FragmentVocabularies) fragments[1];
    }

    private void displayFragment(Fragment fragment) {
        fragment.setEnterTransition(new Fade());
        //fragment.setExitTransition(add_new Fade());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), fragment);
        transaction.commit();
    }

    public Vocabulary getSelectedVocabulary() {
        return mSelectedVocabulary;
    }

    public RealmResults<Vocabulary> getVocabularies()
    {
        return mVocabularies;
    }

    public void setSelectedVocabulary(final Vocabulary vocabulary) {
        mRealm.beginTransaction();
        mSelectedVocabulary = mRealm.copyFromRealm(vocabulary);
        mRealm.commitTransaction();

        vocabulary.removeAllChangeListeners();
        vocabulary.addChangeListener(new RealmObjectChangeListener<RealmModel>() {
            @Override
            public void onChange(RealmModel realmModel, @Nullable ObjectChangeSet changeSet) {
                if (!changeSet.isDeleted())
                    mSelectedVocabulary = mRealm.copyFromRealm(vocabulary);
            }
        });
    }

}