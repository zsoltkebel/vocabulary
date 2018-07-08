package com.example.kebelzsolt.dictionary20.main.vocabulary;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.DBHelper;
import com.example.kebelzsolt.dictionary20.ExtendedEditText;
import com.example.kebelzsolt.dictionary20.R;
import com.example.kebelzsolt.dictionary20.Vocabulary;
import com.example.kebelzsolt.dictionary20.Word;
import com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.kebelzsolt.dictionary20.Data.numOfSelectedItems;
import static com.example.kebelzsolt.dictionary20.Data.vocabularies;
import static com.example.kebelzsolt.dictionary20.Preferences.SORT_ABC;
import static com.example.kebelzsolt.dictionary20.Preferences.SORT_DATE;
import static com.example.kebelzsolt.dictionary20.Preferences.getSortType;
import static com.example.kebelzsolt.dictionary20.Preferences.setSortType;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_CODE_OF_VOCABULARY;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.colorTextGrey;

public class ActivityVocabulary extends AppCompatActivity {

    public static final String PREFS_VOCABULARY = "prefVocabulary";
    public static final String PREFS_INDEX = "prefIndex";

    public static final String KEY_WORD_ID = "indexOfWord";
    public static final String KEY_VOCABULARY_ID = "vocabularyID";



    public ExtendedEditText edtxt_search;
    private static LinearLayout lyt_options;          //The layout that contains @Button_Edit, @Button_DeleteSelected

    @BindView(R.id.imgvClear)
    protected ImageView clearEditText;

    @BindView(R.id.imageView_wordList_flag)
    protected ImageView icon;

    @BindView(R.id.txt_language)
    protected TextView txtv_name;

    private static TextView txtv_numOfWords;

    RelativeLayout touch;
    public static ProgressBar pbSearch;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static Vocabulary vocabulary;

    public static int vocabularyID;
    public static int vocabularyIndex;

    public static String filter = null;

    public static boolean selectable = false;

    static Context context;

    public static PagerAdapter adapter;

    private FragmentPhraseList fragmentPhrases;
    private FragmentAddPhrase fragmentAddPhrase;

    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);
        ButterKnife.bind(this);

        database = new DBHelper(getBaseContext());
        // to scroll automatically if the text is too long
        txtv_name.setSelected(true);

        edtxt_search          = (ExtendedEditText) findViewById(R.id.editText);
        lyt_options           = (LinearLayout) findViewById(R.id.layout_options);
        txtv_numOfWords       = (TextView) findViewById(R.id.txt_num_of_words);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        touch = (RelativeLayout) findViewById(R.id.touch);
        pbSearch = (ProgressBar) findViewById(R.id.progressBar3);
        pbSearch.setIndeterminate(true);

        vocabularyID = getIntent().getExtras().getInt(KEY_VOCABULARY_ID);
        vocabularyIndex = getIntent().getExtras().getInt(KEY_INDEX_OF_VOCABULARY);


        vocabulary = database.getVocabulary(vocabularyID);
        numOfSelectedItems = 0;

        context = this;

        selectable = false;
        titleRefresh();

        setPrefs();
        setUpTabs();
        setStatusBar();


        touch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tabLayout.getSelectedTabPosition() == 0)
                    clearFocus();
                return false;
            }
        });

        edtxt_search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                pbSearch.setVisibility(View.VISIBLE);
                filter = edtxt_search.getText().toString();
                if (filter == null || filter.equals("")) {
                    clearEditText.setVisibility(View.GONE);
                } else {
                    clearEditText.setVisibility(View.VISIBLE);
                }
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });

        edtxt_search.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    edtxt_search.setHint("");
                    tabLayout.getTabAt(0).select();
                    hideKeyboard(false);
                }
                else
                {
                    edtxt_search.setHint(R.string.search);
                }
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 1)
                {
                    ((FragmentAddPhrase) adapter.getItem(position)).focusPhraseEditText();
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

        setColorChange();
    }

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500))
            {
                pbSearch.setVisibility(View.VISIBLE);
                search(filter);
            }
        }
    };
    private Runnable input_finish_checker2 = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + 0 - 500))
            {
                pbSearch.setVisibility(View.VISIBLE);
                search(filter);
            }
        }
    };


    private void setPrefs()
    {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE).edit();
        editor.putInt(PREFS_INDEX, vocabularyID);
        editor.apply();
    }

    private void setUpTabs()
    {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Phrases));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.add));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Bundle arguments = new Bundle();
        arguments.putInt(KEY_VOCABULARY_ID, vocabularyID);
        adapter = new PagerAdapter(getSupportFragmentManager(), ActivityVocabulary.this,
                arguments);

        fragmentPhrases = (FragmentPhraseList) adapter.getItem(0);
        fragmentAddPhrase = (FragmentAddPhrase) adapter.getItem(1);

        viewPager.setAdapter(adapter);


        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(R.string.Phrases);
        tabLayout.getTabAt(1).setText(R.string.Add);

        tabLayout.getTabAt(0).select();
    }

    public void rcListRefresh(int position)
    {
        ((FragmentPhraseList)adapter.getItem(0)).listRefresh(position);
    }

    public void clearFocus()
    {
        edtxt_search.clearFocus();
        edtxt_search.setHint(R.string.search);
        hideKeyboard(true);
    }
    public void clearEditText()
    {
        if (!edtxt_search.getText().toString().equals(""))
            edtxt_search.setText("");
    }

    @Override
    public void onBackPressed()
    {
        if (fragmentPhrases.isSelectable()) {
            fragmentPhrases.setSelectable(false);
            numOfSelectedItems = 0;
            for (Word word : vocabulary.getWordList())
                if (word.isSelected())
                    word.setSelected(false);

            //optionsRefresh();
        } else {
            if (tabLayout.getSelectedTabPosition() != 0)
                tabLayout.getTabAt(0).select();
            else
                finish();
        }
    }

    private boolean search(String filter)
    {
        fragmentPhrases.search(filter);

        if (filter == null || filter.equals("")) {
            clearEditText.setVisibility(View.INVISIBLE);
            return false;
        } else {
            clearEditText.setVisibility(View.VISIBLE);
            return true;
        }
    }

    public void clearSearch(View view)
    {
        pbSearch.setVisibility(View.VISIBLE);
        edtxt_search.setText("");
        clearFocus();

        handler.postDelayed(input_finish_checker2, 0);
        //search(filter);
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

    public void titleRefresh()
    {
        vocabulary = database.getVocabulary(vocabularyID);

        icon.setImageDrawable(vocabulary.getIconDrawable(context));
        txtv_name.setText(vocabulary.getTitle());
        txtv_numOfWords.setText(String.valueOf(vocabulary.getNumOfWords()));
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
                        if (vocabularies.get(vocabularyIndex).getWordList().size() != 0) {
                            Intent intent = new Intent(ActivityVocabulary.this, ActivityLearnConfiguration.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(KEY_INDEX_OF_VOCABULARY, vocabularyIndex);
                            intent.putExtras(bundle);
                            //finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(ActivityVocabulary.this, "There isn't any word.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.ordering:
                        // open ordering select menu
                        final View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_sort_picker, null);
                        final RadioGroup rg_sort = (RadioGroup) dialog_layout.findViewById(R.id.radioGroup_sort);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        rg_sort.getChildAt(0).setId(SORT_DATE);
                        rg_sort.getChildAt(1).setId(SORT_ABC);

                        ((RadioButton)rg_sort.getChildAt(getSortType(context))).setChecked(true);

                        builder.setView(dialog_layout);
                        Spanned title = Html.fromHtml("<font color='" + colorTextGrey + "'>" +
                                "Order by" + "</font>");
                        builder.setTitle(title);
                        builder.setIcon(R.drawable.sort_icon);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setSortType(context, rg_sort.getCheckedRadioButtonId());

                                Bundle dataBundle = new Bundle();
                                dataBundle.putString(KEY_CODE_OF_VOCABULARY, vocabulary.getCode());
                                dataBundle.putInt(KEY_VOCABULARY_ID, vocabularyID);
                                Intent intent = new Intent(getBaseContext(), ActivityVocabulary.class);
                                intent.putExtras(dataBundle);
                                startActivity(intent);
                                finish();
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
        tabLayout.getTabAt(1).select();
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

    public FragmentPhraseList getFragmentPhrases()
    {
        return fragmentPhrases;
    }
    public FragmentAddPhrase getFragmentAddPhrase()
    {
        return fragmentAddPhrase;
    }

    private void setColorChange()
    {
        //TEST--------------------------------------------
        final RelativeLayout header = (RelativeLayout) findViewById(R.id.header);

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



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
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
                    ((FragmentAddPhrase) adapter.getItem(position)).focusPhraseEditText();
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
}
