package com.example.kebelzsolt.dictionary20.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kebelzsolt.dictionary20.R;
import com.example.kebelzsolt.dictionary20.main.settings.ActivitySettings;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.listRefresh;
import static com.example.kebelzsolt.dictionary20.main.settings.ActivitySettings.PREFS_SETTINGS;
import static com.example.kebelzsolt.dictionary20.user.ActivityUserMenu.FEMALE;
import static com.example.kebelzsolt.dictionary20.user.ActivityUserMenu.MALE;
import static com.example.kebelzsolt.dictionary20.user.ActivityUserMenu.PREFS_GENDER;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class ActivityTabLayout extends AppCompatActivity {

    private static TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter adapter;

    boolean first = true;

    private static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        activity = this;

        populateTablayout();
        setTabIcons();
        getPrefs();
        setStatusBar(getResources().getColor(R.color.dark));

        setColorChange();

    }

    @Override
    public void onRestart() {

        populateTablayout();
        setTabIcons();
        listRefresh();
        getPrefs();
        super.onRestart();
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

    public void openSettings(View view)
    {
        Intent intent = new Intent(getBaseContext(), ActivitySettings.class);
        startActivity(intent);
    }

    public void getPrefs()
    {
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        int gender = prefs.getInt(PREFS_GENDER, MALE); //0 is the default value.

        switch (gender)
        {
            case MALE:
                avatar.setImageDrawable(getResources().getDrawable(R.drawable.student_male));
                break;
            case FEMALE:
                avatar.setImageDrawable(getResources().getDrawable(R.drawable.student_female));
        }
    }

    public static void finishActivity()
    {
        activity.finish();
    }

    private void populateTablayout()
    {
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new PagerAdapter(getSupportFragmentManager(), ActivityTabLayout.this);

        mViewPager.setAdapter(adapter);


        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(1).select();
    }

    public void setTabIcons()
    {
        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_import_dark);
        mTabLayout.getTabAt(1).setIcon(R.drawable.list_active);
        mTabLayout.getTabAt(2).setIcon(R.drawable.tab_add_dark);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_import_active);
                        break;
                    case 1:
                        mTabLayout.getTabAt(1).setIcon(R.drawable.list_active);
                        break;
                    case 2:
                        mTabLayout.getTabAt(2).setIcon(R.drawable.list_add_active);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_import_dark);
                        break;
                    case 1:
                        mTabLayout.getTabAt(1).setIcon(R.drawable.tab_list_dark);
                        break;
                    case 2:
                        mTabLayout.getTabAt(2).setIcon(R.drawable.tab_add_dark);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static void fresh()
    {
        mTabLayout.getTabAt(1).select();
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

    private void setColorChange()
    {
        //TEST--------------------------------------------
        final LinearLayout header = (LinearLayout) findViewById(R.id.header);

        //get the image button by id
        ImageView myImg1 = (ImageView) findViewById(R.id.left_shadow);
        ImageView myImg2 = (ImageView) findViewById(R.id.right_shadow);
        final LinearLayout leftSpacer = (LinearLayout) findViewById(R.id.left_spacer);
        final LinearLayout rightSpacer = (LinearLayout) findViewById(R.id.right_spacer);

//get drawable from image button
        final GradientDrawable drawable1 = (GradientDrawable) myImg1.getBackground();
        final GradientDrawable drawable2 = (GradientDrawable) myImg2.getBackground();

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

                leftSpacer.setBackgroundColor(color);
                rightSpacer.setBackgroundColor(color);
                header.setBackgroundColor(color);
                drawable1.setColors(colorsBack);
                drawable2.setColors(colorsBack);
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

                }*/
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //finishAffinity();
    }

}