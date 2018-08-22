package com.vocabulary.screens.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.Subject;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.more.ActivityMore;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.vocabulary.screens.settings.ActivitySettings.PREFS_SETTINGS;
import static com.vocabulary.user.ActivityUserMenu.FEMALE;
import static com.vocabulary.user.ActivityUserMenu.MALE;
import static com.vocabulary.user.ActivityUserMenu.PREFS_GENDER;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class ActivityTabLayout extends RealmActivity {

    public static final int ITEM_DELETED = 1;
    public static final int NO_ACTION = 0;

    @BindView(R.id.tab_layout)
            protected TabLayout mTabLayout;
    @BindView(R.id.viewpager)
            protected ViewPager mViewPager;
    @BindView(R.id.fl_fragment_frame)
            protected FrameLayout frameLayout;

    private RealmResults<Vocabulary> vocabularies = null;
    private Vocabulary mSelectedVocabulary;

    private Fragment[] fragments = new Fragment[]{
            new FragmentHome(),
            new FragmentVocabularies(),
            new FragmentAddVocabulary(),
            new FragmentSettings()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);
        ButterKnife.bind(this);

        //Must call once in the application
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        populateTablayout();
        setTabIcons();
        setWindowColors(getResources().getColor(R.color.page2_dark));
        setStatusBarBright(true);

        getPrefs();
        //setStatusBar(getResources().getColor(R.color.dark));

        displayFragment(fragments[1]);

        setColorChange();


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

        vocabularies = mRealm.where(Vocabulary.class).sort(Vocabulary.SORT_STRING).findAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case ITEM_DELETED:
                Snackbar snackbar = Snackbar.make(fragments[1].getView().findViewById(R.id.background),
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
    public void onBackPressed() {

        if (mTabLayout.getSelectedTabPosition() != 1)
        {
            mTabLayout.getTabAt(1).select();
        }
        else
            super.onBackPressed();
    }

    public void startActivityMore(String vocabularyId) {
        Intent intent = new Intent(this, ActivityMore.class);
        intent.putExtra(Vocabulary.ID, vocabularyId);
        startActivityForResult(intent, NO_ACTION);
    }

    public void getPrefs()
    {
        //ImageView avatar = (ImageView) findViewById(R.id.avatar);
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        int gender = prefs.getInt(PREFS_GENDER, MALE); //0 is the default value.

        switch (gender)
        {
            case MALE:
                //avatar.setImageDrawable(getResources().getDrawable(R.drawable.student_male));
                break;
            case FEMALE:
                //avatar.setImageDrawable(getResources().getDrawable(R.drawable.student_female));
        }
    }


    private void populateTablayout()
    {
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());



        //mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(1).select();
    }

    public void setTabIcons()
    {
        View customTabView1 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ImageView ivIcon1 = (ImageView) customTabView1.findViewById(R.id.iv_icon);
        TextView tvText1 = (TextView) customTabView1.findViewById(R.id.tv_text);

        ivIcon1.setImageDrawable(getDrawable(R.drawable.new_home2));
        tvText1.setText(getResources().getString(R.string.tab_home));
        tvText1.setTextColor(getResources().getColor(R.color.black));
        tvText1.setTypeface(tvText1.getTypeface(), Typeface.NORMAL);
        mTabLayout.getTabAt(0).setCustomView(customTabView1);

        View customTabView2 = LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ImageView ivIcon2 = (ImageView) customTabView2.findViewById(R.id.iv_icon);
        TextView tvText2 = (TextView) customTabView2.findViewById(R.id.tv_text);

        ivIcon2.setImageDrawable(getDrawable(R.drawable.new_book_selected));
        tvText2.setText(getResources().getString(R.string.tab_vocabularies));
        tvText2.setTextColor(getResources().getColor(R.color.colorAccent));
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

        //mTabLayout.getTabAt(0).setIcon(R.drawable.tab_import_dark);
        //mTabLayout.getTabAt(1).setIcon(R.drawable.new_home_selected);
        //mTabLayout.getTabAt(2).setIcon(R.drawable.tab_add_dark);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        View customTabView1 = tab.getCustomView();
                        ImageView ivIcon1 = (ImageView) customTabView1.findViewById(R.id.iv_icon);
                        TextView tvText1 = (TextView) customTabView1.findViewById(R.id.tv_text);

                        ivIcon1.setImageDrawable(getDrawable(R.drawable.new_home2_selected));
                        tvText1.setTextColor(getResources().getColor(R.color.colorAccent));
                        tvText1.setTypeface(tvText1.getTypeface(), Typeface.BOLD);
                        mTabLayout.getTabAt(0).setCustomView(customTabView1);
                        break;
                    case 1:
                        View customTabView2 = tab.getCustomView();
                        ImageView ivIcon2 = (ImageView) customTabView2.findViewById(R.id.iv_icon);
                        TextView tvText2 = (TextView) customTabView2.findViewById(R.id.tv_text);

                        ivIcon2.setImageDrawable(getDrawable(R.drawable.new_book_selected));
                        tvText2.setTextColor(getResources().getColor(R.color.colorAccent));
                        tvText2.setTypeface(tvText2.getTypeface(), Typeface.BOLD);
                        mTabLayout.getTabAt(1).setCustomView(customTabView2);
                        break;
                    case 2:
                        View customTabView3 = tab.getCustomView();
                        ImageView ivIcon3 = (ImageView) customTabView3.findViewById(R.id.iv_icon);
                        TextView tvText3 = (TextView) customTabView3.findViewById(R.id.tv_text);

                        ivIcon3.setImageDrawable(getDrawable(R.drawable.new_add_selected));
                        tvText3.setTextColor(getResources().getColor(R.color.colorAccent));
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

                        ivIcon1.setImageDrawable(getDrawable(R.drawable.new_home2));
                        tvText1.setTextColor(getResources().getColor(R.color.black));
                        tvText1.setTypeface(null, Typeface.NORMAL);
                        mTabLayout.getTabAt(0).setCustomView(customTabView1);
                        break;
                    case 1:
                        View customTabView2 = tab.getCustomView();
                        ImageView ivIcon2 = (ImageView) customTabView2.findViewById(R.id.iv_icon);
                        TextView tvText2 = (TextView) customTabView2.findViewById(R.id.tv_text);

                        ivIcon2.setImageDrawable(getDrawable(R.drawable.new_book));
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

    public void setStatusBar(Integer color)
    {
        int vocabularyColor = R.color.dark;
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


// finally change the color
        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
    }

    private void setStatusBarBright(boolean bright) {
        if (bright) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {

        }
    }
    public void setWindowColors(Integer color) {
        getWindow().setStatusBarColor(color);
        getWindow().setNavigationBarColor(color);
    }

    private void setColorChange()
    {
        //TEST--------------------------------------------
        final LinearLayout header = (LinearLayout) findViewById(R.id.header);

        //get the image button by id
        //final LinearLayout leftSpacer = (LinearLayout) findViewById(R.id.left_spacer);
        //final LinearLayout rightSpacer = (LinearLayout) findViewById(R.id.right_spacer);

//get drawable from image button

//set color


        final int[] colors = {getResources().getColor(R.color.accent_light), getResources().getColor(R.color.dark), getResources().getColor(R.color.back_grey)};
        final ArgbEvaluator argbEvaluator = new ArgbEvaluator();


        Integer color1 = getResources().getColor(R.color.page1);
        Integer color2 = getResources().getColor(R.color.page2);
        Integer color3 = getResources().getColor(R.color.page3);

        Integer color1_dark = getResources().getColor(R.color.page1_dark);
        Integer color2_dark = getResources().getColor(R.color.page2_dark);
        Integer color3_dark = getResources().getColor(R.color.page3_dark);

        final Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


// finally change the color


        final ValueAnimator mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), color1, color2, color3);
        final ValueAnimator mColorAnimationDark = ValueAnimator.ofObject(new ArgbEvaluator(), color1_dark, color2_dark, color3_dark);

        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Integer color = (Integer)animator.getAnimatedValue();
                int[] colorsBack = {color,  getResources().getColor(R.color.transplarent)};

                //leftSpacer.setBackgroundColor(color);
                //rightSpacer.setBackgroundColor(color);
                header.setBackgroundColor(color);
            }
        });
        mColorAnimation.setDuration((3 - 1) * 10000000000l);

        mColorAnimationDark.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer color = (Integer)animation.getAnimatedValue();
                window.setStatusBarColor(color);
                window.setNavigationBarColor(color);
            }
        });
        mColorAnimationDark.setDuration((3 - 1) * 10000000000l);


/*
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mColorAnimation.setCurrentPlayTime((long)((positionOffset + position)* 10000000000l));
                mColorAnimationDark.setCurrentPlayTime((long)((positionOffset + position)* 10000000000l));
                /*
                if(position < (adapter.getCount() -1) && position < (colors.length - 1)) {

                    mTabLayout.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                    setStatusBar((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));

                } else {

                    mTabLayout.setBackgroundColor(colors[colors.length - 1]);
                    setStatusBar(colors[colors.length - 1]);

                }
                           }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
        window.setStatusBarColor(color2_dark);
        window.setNavigationBarColor(color2_dark);
    }

    public Realm getRealm() {
        return mRealm;
    }

    public FragmentVocabularies getFragmentVocabularyList() {
        return (FragmentVocabularies) fragments[1];
    }

    public RealmResults<Vocabulary> getVocabularies()
    {
        return vocabularies;
    }

    public void hideKeyboard(View view) {
        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void displayFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(frameLayout.getId(), fragment);
        t.commit();
    }

    public void setSelectedVocabulary(Vocabulary vocabulary) {
        mSelectedVocabulary = vocabulary;
    }

    public Vocabulary getSelectedVocabulary() {
        return mSelectedVocabulary;
    }
}
