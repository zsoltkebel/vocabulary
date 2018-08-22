package com.vocabulary.screens.vocabulary;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.vocabulary.Preferences.SORT_ABC_PHRASE;
import static com.vocabulary.Preferences.SORT_DATE;
import static com.vocabulary.screens.vocabulary.FragmentPhrases.PREFS_SORTING_ASCENDING;
import static com.vocabulary.screens.vocabulary.FragmentPhrases.PREFS_SORTING_FIELD;

public class ActivityVocabulary extends RealmActivity {
    public static final String PREFS_VOCABULARY = "prefVocabulary";
    public static final String PREFS_INDEX = "prefIndex";

    public static final int SORT_ABC_MEANING = 2;

    @BindView(R.id.imageView_wordList_flag)
    protected ImageView mIvIcon;
    @BindView(R.id.v_clear_search)
    protected ImageView mIvClearSearch;

    @BindView(R.id.txt_language)
    protected TextView mTvTitle;
    @BindView(R.id.txt_num_of_words)
    protected TextView mTvNumOfPhrases;

    @BindView(R.id.et_search)
    protected EditText mEtSearch;

    @BindView(R.id.v_filter)
    protected View mViewFilter;

    @BindView(R.id.pb_filtering)
    protected ProgressBar mPbFiltering;

    @BindView(R.id.touch)
    protected RelativeLayout mRltTouch;

    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;


    private String mFilter = null;

    private PagerAdapter mPagerAdapter = null;

    private FragmentPhrases fragmentPhrases = null;
    private FragmentAddPhrase fragmentAddPhrase = null;

    private Vocabulary mVocabulary = null;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        ButterKnife.bind(this);

        // to scroll automatically if the text is too long
        mTvTitle.setSelected(true);

        String vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        mVocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();


        titleRefresh();

        setUpTabs();
        setStatusBar();


        mRltTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEtSearch.clearFocus();
                if (mTabLayout.getSelectedTabPosition() == 0) {
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
                }
                return false;
            }
        });

        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    navigateToTab(0);
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 1)
                {
                    ((FragmentAddPhrase) mPagerAdapter.getItem(position)).focusPhraseEditText();
                }
                else
                {
                    hideKeyboard(true);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        final Handler handler = new Handler();
        final Runnable filtering = new Runnable() {
            @Override
            public void run() {
                mPbFiltering.setVisibility(View.VISIBLE);
                //if (getFragmentPhrases().getAdapter() != null)
                    //getFragmentPhrases().getAdapter().getFilter().filter(getFilterString());
                //else
                    //Toast.makeText(ActivityVocabulary.this, "error", Toast.LENGTH_SHORT).show();
            }
        };


/*
        edtxt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    edtxt_search.clearFocus();
                    hideKeyboard(true);
                }
                return true;
            }
        });
*/
        setColorChange();

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilter = s.toString();
                if (!mFilter.equals("")) {
                    mViewFilter.setActivated(true);
                    mIvClearSearch.setVisibility(View.VISIBLE);
                } else {
                    mViewFilter.setActivated(false);
                    mIvClearSearch.setVisibility(View.INVISIBLE);
                }
                //mPbFiltering.setVisibility(View.VISIBLE);
                ((FragmentPhrases) mPagerAdapter.getItem(0)).getAdapter().setFilter(mRealm, mFilter, getSortingField(), isSortingAscending());

                //getFragmentPhrases().getAdapter().getFilter().filter(getFilterString());
                /*
                try {
                    handler.removeCallbacks(setFilter);
                } catch (Exception e) {}

                try {
                    handler.post(setFilter);
                } catch (Exception e) {
                    setUpTabs();
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void setSorting(String sortingField, boolean ascending) {
        SharedPreferences prefs = getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        prefs.edit().putString(PREFS_SORTING_FIELD, sortingField).apply();
        prefs.edit().putBoolean(PREFS_SORTING_ASCENDING, ascending).apply();
    }

    public String getSortingField() {
        SharedPreferences prefs = getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getString(PREFS_SORTING_FIELD, Phrase.DATE);
    }

    public boolean isSortingAscending() {
        SharedPreferences prefs = getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getBoolean(PREFS_SORTING_ASCENDING, false);
    }


    public String getFilterString() {
        return mFilter;
    }

    public ProgressBar getProgressBar() {
        return mPbFiltering;
    }

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;



    public void setSearchEnabled(boolean enabled) {
        mEtSearch.setEnabled(enabled);
    }

    @OnClick(R.id.v_clear_search)
    protected void onClearSearchClicked() {
        mEtSearch.setText("");
    }


    public void navigateToTab(int position) {
        mTabLayout.getTabAt(position).select();
    }

    private void setUpTabs()
    {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.Phrases));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.add));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        fragmentPhrases = (FragmentPhrases) mPagerAdapter.getItem(0);
        fragmentAddPhrase = (FragmentAddPhrase) mPagerAdapter.getItem(1);

        mViewPager.setAdapter(mPagerAdapter);


        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText(R.string.Phrases);
        mTabLayout.getTabAt(1).setText(R.string.Add);

        mTabLayout.getTabAt(0).select();
    }

    public void rcListRefresh(int position)
    {
        ((FragmentPhrases) mPagerAdapter.getItem(0)).listRefresh(position);
    }


    public CharSequence getFilterCharSequence() {
        return mEtSearch.getText();
    }

    public void clearFilter() {
        mEtSearch.setText("");
    }

    @Override
    public void onBackPressed()
    {
        if (fragmentPhrases.isSelectable()) {
            fragmentPhrases.setSelectable(false);
            //for (Word word : mVocabulary.getWordList())
            //   if (word.isSelected())
            //        word.setSelected(false);

            //optionsRefresh();
        } else {
            if (mTabLayout.getSelectedTabPosition() != 0)
                mTabLayout.getTabAt(0).select();
            else
                finish();
        }
    }





    private void hideKeyboard(boolean hide)
    {
        final InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        if (hide)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        else
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }



    public void backToDictionaries(View view)
    {
        finish();
        hideKeyboard(true);
    }

    public void titleRefresh() {
        mIvIcon.setImageDrawable(mVocabulary.getIconDrawable(this));
        mTvTitle.setText(mVocabulary.getTitle());
        mTvNumOfPhrases.setText(String.valueOf(mVocabulary.getPhrases().size()));
    }

    public void setNumOfPhrasesText() {
        if (mTvNumOfPhrases != null)
            mTvNumOfPhrases.setText(String.valueOf(mVocabulary.getPhrases().size()));
    }


    public void openMenu(View view) {
        final PopupMenu popup = new PopupMenu(ActivityVocabulary.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_vocabulary, popup.getMenu());


        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.learn:
                        /*
                        if (vocabularies.get(vocabularyIndex).getWordList().size() != 0) {
                            Intent intent = new Intent(ActivityVocabulary.this, ActivityLearnConfiguration.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(KEY_INDEX_OF_VOCABULARY, vocabularyIndex);
                            intent.putExtras(bundle);
                            //finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(ActivityVocabulary.this, "There isn't any word.", Toast.LENGTH_SHORT).show();
                        }*/
                        return true;
                    case R.id.ordering:
                        // open ordering select menu
                        final View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_sort_picker, null);
                        final RadioGroup rg_sort = (RadioGroup) dialog_layout.findViewById(R.id.radioGroup_sort);
                        final Switch ascendingSwitch = (Switch) dialog_layout.findViewById(R.id.sw_ascending);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVocabulary.this);

                        rg_sort.getChildAt(0).setId(SORT_DATE);
                        rg_sort.getChildAt(1).setId(SORT_ABC_PHRASE);
                        rg_sort.getChildAt(2).setId(SORT_ABC_MEANING);

                        rg_sort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == SORT_DATE)
                                    ascendingSwitch.setEnabled(true);
                                else
                                    ascendingSwitch.setEnabled(false);
                            }
                        });

                        switch (getFragmentPhrases().getSortingField()) {
                            case Phrase.DATE:
                                ((RadioButton) rg_sort.getChildAt(0)).setChecked(true);
                                break;
                            case Phrase.P1:
                                ((RadioButton) rg_sort.getChildAt(1)).setChecked(true);
                                break;
                            case Phrase.P2:
                                ((RadioButton) rg_sort.getChildAt(2)).setChecked(true);
                                break;
                        }

                        ascendingSwitch.setChecked(!getFragmentPhrases().getSortingAscending());

                        builder.setView(dialog_layout);
                        builder.setTitle(R.string.sort_title);

                        builder.setPositiveButton(R.string.sort, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean ascending = ascendingSwitch.isChecked();

                                switch (rg_sort.getCheckedRadioButtonId()) {
                                    case SORT_DATE:
                                        getFragmentPhrases().setSorting(Phrase.DATE, !ascending);
                                        getFragmentPhrases().setAdapter();
                                        break;
                                    case SORT_ABC_PHRASE:
                                        getFragmentPhrases().setSorting(Phrase.P1, true);
                                        getFragmentPhrases().setAdapter();
                                        break;
                                    case SORT_ABC_MEANING:
                                        getFragmentPhrases().setSorting(Phrase.P2, true);
                                        getFragmentPhrases().setAdapter();
                                        break;
                                }
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, null);
                        builder.create().show();

                        return true;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    public void goToAdd(View view)
    {
        mTabLayout.getTabAt(1).select();
    }

    public void setStatusBar()
    {
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getResources().getColor(R.color.darker));
        window.setNavigationBarColor(getResources().getColor(R.color.darker));
    }

    public FragmentPhrases getFragmentPhrases()
    {
        return (FragmentPhrases) mPagerAdapter.getItem(0);
    }
    public FragmentAddPhrase getFragmentAddPhrase()
    {
        return fragmentAddPhrase;
    }

    private void setColorChange()
    {
        //TEST--------------------------------------------
        final ConstraintLayout header = (ConstraintLayout) findViewById(R.id.header);

//set color


        final int[] colors = {getResources().getColor(R.color.accent_light), getResources().getColor(R.color.dark), getResources().getColor(R.color.back_grey)};
        final ArgbEvaluator argbEvaluator = new ArgbEvaluator();


        Integer color1 = getResources().getColor(R.color.page2);
        Integer color2 = getResources().getColor(R.color.page3);

        Integer color1_dark = getResources().getColor(R.color.page2_dark);
        Integer color2_dark = getResources().getColor(R.color.page3_dark);

        final Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


// finally change the color


        final ValueAnimator mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), color1, color2);
        final ValueAnimator mColorAnimationDark = ValueAnimator.ofObject(new ArgbEvaluator(), color1_dark, color2_dark);

        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Integer color = (Integer)animator.getAnimatedValue();
                header.setBackgroundColor(color);
            }
        });
        mColorAnimation.setDuration((2 - 1) * 10000000000l);

        mColorAnimationDark.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer color = (Integer)animation.getAnimatedValue();
                window.setStatusBarColor(color);
                window.setNavigationBarColor(color);
            }
        });
        // (3 - 1) = number of pages minus 1
        mColorAnimationDark.setDuration((2 - 1) * 10000000000l);



        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mColorAnimation.setCurrentPlayTime((long)((positionOffset + position)* 10000000000l));
                mColorAnimationDark.setCurrentPlayTime((long)((positionOffset + position)* 10000000000l));
            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 1)
                {
                    ((FragmentAddPhrase) mPagerAdapter.getItem(position)).focusPhraseEditText();
                }
                else
                {
                    hideKeyboard(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public Vocabulary getVocabulary() {
        return mVocabulary;
    }

    public Realm getRealm() {
        return mRealm;
    }
}
