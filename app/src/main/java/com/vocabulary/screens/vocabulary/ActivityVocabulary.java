package com.vocabulary.screens.vocabulary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.FragmentVocabularyInfo;
import com.vocabulary.screens.learnconfig.ActivityLearnConfig;
import com.vocabulary.screens.main.ActivityMain;
import com.vocabulary.screens.more.ActivityMore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vocabulary.Preferences.SORT_ABC_PHRASE;
import static com.vocabulary.Preferences.SORT_DATE;
import static com.vocabulary.screens.vocabulary.FragmentPhrases.PREFS_SORTING_ASCENDING;
import static com.vocabulary.screens.vocabulary.FragmentPhrases.PREFS_SORTING_FIELD;

public class ActivityVocabulary extends RealmActivity {
    public static final String PREFS_VOCABULARY = "prefVocabulary";
    public static final String PREFS_INDEX = "prefIndex";

    public static final int SORT_ABC_MEANING = 2;

    @BindView(R.id.v_clear_search)
    protected ImageView mIvClearSearch;

    @BindView(R.id.et_search)
    protected EditText mEtSearch;

    @BindView(R.id.v_filter)
    protected View mViewFilter;

    @BindView(R.id.pb_filtering)
    protected ProgressBar mPbFiltering;

    @BindView(R.id.touch)
    protected RelativeLayout mRltTouch;
    @BindView(R.id.flt_vocabulary_info)
    protected FrameLayout mFltVocabularyInfo;

    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;


    private String mFilter = null;

    private PagerAdapter mPagerAdapter = null;

    private FragmentPhrases mFragmentPhrases = null;
    private FragmentVocabularyInfo mFragmentVocabularyInfo;

    private Vocabulary mVocabulary = null;

    public static Intent createIntent(Context context, String vocabularyId) {
        Intent intent = new Intent(context, ActivityVocabulary.class);
        intent.putExtra(Vocabulary.ID, vocabularyId);

        return intent;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        ButterKnife.bind(this);

        String vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        mVocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();

        setUpTabs();

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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (((FragmentAddPhrase) mPagerAdapter.getItem(1)).mEtPhrase.getText().toString().equals(""))
                   ((FragmentAddPhrase) mPagerAdapter.getItem(1)).mEtPhrase.setText("");
            }

            @Override
            public void onPageSelected(int position) {
                if (position > 0) {
                    mEtSearch.clearFocus();
                    ((FragmentAddPhrase) mPagerAdapter.getItem(1)).mEtPhrase.requestFocus();
                    //((FragmentAddPhrase) mPagerAdapter.getItem(1)).mEtPhrase.clearFocus();

                    setKeyboardVisible(null, true);
                } else
                    setKeyboardVisible(mViewPager, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    navigateToTab(0);
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
                ((FragmentPhrases) mPagerAdapter.getItem(0)).getAdapter().setFilter(mRealm, mFilter, getSortingField(), isSortingAscending());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // set mVocabulary info line
        mFragmentVocabularyInfo = FragmentVocabularyInfo.newInstance(mVocabulary.getIconInt(this), mVocabulary.getTitle(), mVocabulary.getPhrases().size());
        mFragmentVocabularyInfo.displayFragment(this, mFltVocabularyInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case ActivityMain.VOCABULARY_DELETED:
                setResult(ActivityMain.VOCABULARY_DELETED);
                finish();
                break;
            case ActivityMain.VOCABULARY_RENAMED:
                mFragmentVocabularyInfo.updateInfo(mVocabulary.getTitle(), mVocabulary.getPhrases().size());
                break;
            case ActivityMain.VOCABULARY_MERGED:
                setResult(ActivityMain.VOCABULARY_MERGED);
                finish();
                break;
            default:
                break;
        }
    }

    public void setKeyboardVisible(View view, boolean visible) {
        if (visible) {
            //show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else {
            //hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setNumOfPhrasesText() {
        mFragmentVocabularyInfo.updateInfo(mVocabulary.getTitle(), mVocabulary.getPhrases().size());
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

        mFragmentPhrases = (FragmentPhrases) mPagerAdapter.getItem(0);

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
        if (mFragmentPhrases.isSelectable()) {
            mFragmentPhrases.setSelectable(false);
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


    public void openMenu(View view) {
        final PopupMenu popup = new PopupMenu(ActivityVocabulary.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_vocabulary, popup.getMenu());


        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.learn:
                        intent = ActivityLearnConfig.createIntent(ActivityVocabulary.this, mVocabulary.getId());
                        startActivity(intent);
                        /*
                        if (vocabularies.get(vocabularyIndex).getWordList().size() != 0) {
                            Intent intent = add_new Intent(ActivityVocabulary.this, ActivityLearnConfig.class);
                            Bundle bundle = add_new Bundle();
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
                    case R.id.more:
                        intent = ActivityMore.createIntent(ActivityVocabulary.this, mVocabulary.getId());
                        startActivityForResult(intent, 1);
                        return true;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    public FragmentPhrases getFragmentPhrases()
    {
        return (FragmentPhrases) mPagerAdapter.getItem(0);
    }

    public Vocabulary getVocabulary() {
        return mVocabulary;
    }
}
