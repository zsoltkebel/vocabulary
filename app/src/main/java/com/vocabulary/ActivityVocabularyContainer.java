package com.vocabulary;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.learn.LearnActivity;

import java.util.ArrayList;

import static com.vocabulary.Data.mydb;
import static com.vocabulary.Data.numOfSelectedItems;
import static com.vocabulary.Data.vocabularies;
import static com.vocabulary.Subject.LANGUAGE;
import static com.vocabulary.Subject.OTHER;
import static com.vocabulary.screens.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;

public class ActivityVocabularyContainer extends AppCompatActivity {

    public static final String KEY_INDEX_OF_WORD = "indexOfWord";

    private ExtendedEditText edtxt_search;
    private static LinearLayout lyt_options;          //The layout that contains @Button_Edit, @Button_DeleteSelected
    private static ImageButton Button_Edit;                  //Edit selected item, this button is on the @lyt_options and only visible if one item selected
    private static ImageButton Button_DeleteSelected; //Delete selected items, this button is on the @lyt_options
    private ImageView ImageView_ClearSearch;           //hadle as a button to clear filter in search EditText
    private ImageView imgv_flag;
    private ListView listView;                           //main listview shows the words in the dictionary
    private static TextView txtv_numOfSelected;
    private TextView txtv_name;
    private TextView txtv_numOfWords;

    private Vocabulary vocabulary;

    private ArrayList<Word> searches = new ArrayList<>();

    private int indexOfVocabulary;

    private String vocabularyCode;
    private String filter = null;

    private static boolean selectable = false;
    private boolean inSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_container);

        edtxt_search          = (ExtendedEditText) findViewById(R.id.editText);
        lyt_options           = (LinearLayout) findViewById(R.id.layout_options);
        Button_Edit           = (ImageButton) findViewById(R.id.btn_edit);
        Button_DeleteSelected = (ImageButton) findViewById(R.id.btn_delSelected);
        ImageView_ClearSearch = (ImageView) findViewById(R.id.imgvClear);
        listView              = (ListView) findViewById(R.id.listView);
        txtv_numOfSelected    = (TextView) findViewById(R.id.tv_numOfSelected);
        imgv_flag             = (ImageView) findViewById(R.id.imageView_wordList_flag);
        txtv_name             = (TextView) findViewById(R.id.txt_language);
        txtv_numOfWords       = (TextView) findViewById(R.id.txt_num_of_words);

        indexOfVocabulary = getIntent().getExtras().getInt(KEY_INDEX_OF_VOCABULARY);
        vocabularyCode = vocabularies.get(indexOfVocabulary).getCode();

        vocabulary = vocabularies.get(indexOfVocabulary);
        numOfSelectedItems = 0;

        titleRefresh();
        listRefresh();

        //TODO
        int vocabularyColor;
        switch (vocabulary.getType())
        {
            case LANGUAGE:
                vocabularyColor = R.color.light;
                break;
            case OTHER:
                vocabularyColor = R.color.light2;
                break;
            default:
                vocabularyColor = R.color.dark;
        }
        findViewById(R.id.background).setBackground(getResources().getDrawable(vocabularyColor));
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this ,vocabularyColor));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectable)
                    return true;
                else
                {
                    selectable = true;
                    numOfSelectedItems = 1;

                    if (inSearch)
                        vocabulary.getWordList().get(searches.get(position).getIndexList()).setSelected(true);
                    else
                        vocabulary.getWordList().get(position).setSelected(true);

                    listRefresh();
                    optionsRefresh();
                    return true;
                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(KEY_INDEX_OF_VOCABULARY, indexOfVocabulary);

                if (inSearch)
                    dataBundle.putInt(KEY_INDEX_OF_WORD, searches.get(position).getIndexList());
                else
                    dataBundle.putInt(KEY_INDEX_OF_WORD, position);

                Intent intent = new Intent(ActivityVocabularyContainer.this, ActivityWord.class);
                intent.putExtras(dataBundle);
                startActivity(intent);

            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtxt_search.clearFocus();
                edtxt_search.setHint("Search");
                hideKeyboard(true);
                return false;
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

        edtxt_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edtxt_search.setHint("");
                    hideKeyboard(false);
                } else {
                    edtxt_search.setHint("Search");
                }
            }
        });

        edtxt_search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                filter = edtxt_search.getText().toString();
                listRefresh();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onBackPressed()
    {
        if (selectable) {
            selectable = false;
            numOfSelectedItems = 0;
            for (Word word : vocabularies.get(indexOfVocabulary).getWordList())
                if (word.isSelected())
                    word.setSelected(false);

            optionsRefresh();
            listRefresh();
        } else {
            //Intent i = new Intent(ActivityVocabulary.this, ActivityVocabularyList.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
            //startActivity(i);
        }
    }

    private void listRefresh()
    {
        Parcelable state = listView.onSaveInstanceState();
        searches.clear();

        if (!search(filter))
        {
            ListViewAdapter2 listViewAdapter = new ListViewAdapter2(this, vocabularies.get(indexOfVocabulary).getWordList(), selectable, indexOfVocabulary);
            listView.setAdapter(listViewAdapter);
        }


        listView.onRestoreInstanceState(state);
    }

    private boolean search(String filter)
    {
        searches.clear();

        if (filter == null || filter.equals("")) {
            inSearch = false;
            ImageView_ClearSearch.setVisibility(View.INVISIBLE);
            return false;
        } else {
            inSearch = true;
            ImageView_ClearSearch.setVisibility(View.VISIBLE);
        }

        for (Word word : vocabularies.get(indexOfVocabulary).getWordList())
        {
            if (word.contains(filter))
                searches.add(word);
        }
        listView.setAdapter(new ListViewAdapter2(ActivityVocabularyContainer.this, searches, selectable, indexOfVocabulary));
        return true;
    }

    public void clearSearch(View view)
    {
        edtxt_search.setText("");
        listRefresh();
    }

    private void hideKeyboard(boolean hide)
    {
        final InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        if (hide)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        else
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
/*
    public void addWord(View view)
    {
        edtxt_search.clearFocus();
        final View dialog_layout = getLayoutInflater().inflate(R.layout.dialog, null);
        final EditText wordInput = (EditText) dialog_layout.findViewById(R.id.editText_word);
        final EditText meaningInput = (EditText) dialog_layout.findViewById(R.id.editText_meaning);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        wordInput.setText("");
        meaningInput.setText("");

        builder.setView(dialog_layout);
        builder.setTitle("Adding a new word");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phrase = wordInput.getText().toString();
                String meaning = meaningInput.getText().toString();

                if (phrase.equals("") || meaning.equals("")) {   //if the user didn't fill all the fields
                    Toast.makeText(getBaseContext(), "Fill both fields!", Toast.LENGTH_SHORT).show();
                } else {   //if he did
                    hideKeyboard(true);
                    dialog.dismiss();

                    vocabulary.addWord(new Word(phrase, meaning));

                    edtxt_search.setText("");
                    titleRefresh();

                    String info = "<font color='#FF4081'><b>" + phrase + "</b></font>";
                    Spanned infoText = Html.fromHtml(info + " has been added");
                    Toast.makeText(getBaseContext(), infoText, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wordInput.setText("");
                meaningInput.setText("");
                hideKeyboard(true);
                dialog.dismiss();
            }
        });
        //TODO afte this getbutton - positive

        final AlertDialog dialog = builder.create();
        dialog.show();

        if (filter != null && !filter.equals("")) {
            wordInput.setText(filter);
            wordInput.clearFocus();
            meaningInput.requestFocus();
        }
        hideKeyboard(false);
    }
*/
    public static void optionsRefresh()
    {
        if (selectable)
        {
            Button_Edit.setVisibility(View.VISIBLE);
            Button_DeleteSelected.setVisibility(View.VISIBLE);
            lyt_options.setVisibility(View.VISIBLE);

            if (numOfSelectedItems > 1)
                Button_Edit.setVisibility(View.GONE);
            else if (numOfSelectedItems < 1)
            {
                Button_Edit.setVisibility(View.GONE);
                Button_DeleteSelected.setVisibility(View.GONE);
            }
            txtv_numOfSelected.setText(Integer.toString(numOfSelectedItems));
        }
        else
        {
            lyt_options.setVisibility(View.GONE);
        }

    }

    public void backToDictionaries(View view)
    {
        //Intent intent = new Intent(ActivityVocabularyContainer.this, ActivityVocabularyList.class);
        finish();
        //startActivity(intent);
    }

    public void titleRefresh()
    {
        imgv_flag.setImageDrawable(vocabulary.getIconDrawable(getBaseContext()));
        txtv_name.setText(vocabulary.getTitle());
        txtv_numOfWords.setText(String.valueOf(vocabulary.getNumOfWords()) + " words");
    }




    public void openMenu(View view) {
        final PopupMenu popup = new PopupMenu(this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_vocabulary, popup.getMenu());


        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.learn:
                        if (vocabularies.get(indexOfVocabulary).getWordList().size() != 0) {
                            Intent intent = new Intent(ActivityVocabularyContainer.this, LearnActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(KEY_INDEX_OF_VOCABULARY, indexOfVocabulary);
                            intent.putExtras(bundle);
                            //finish();
                            startActivity(intent);
                        } else {
                            Toast.makeText(ActivityVocabularyContainer.this, "There isn't any word.", Toast.LENGTH_SHORT).show();
                        }
                        return true;

                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    public void deleteSelected(View view) {
        //delete
        //lenullázza a számlálót

        for (Word word : vocabularies.get(indexOfVocabulary).getWordList()) {
            if (word.isSelected()) {
                mydb.deleteWord(vocabularies.get(indexOfVocabulary).getCode(), word);
            }
        }
        String text;

        if (numOfSelectedItems == 0) {
            text = "You didn't select any items";
        } else if (numOfSelectedItems == 1) {
            text = "<font color='#FF4081'><b>1</b></font> item deleted";
        } else {
            text = "<font color='#FF4081'><b>" + numOfSelectedItems + "</b></font>" + " items deleted";
        }
        Spanned message = Html.fromHtml(text);
        Toast.makeText(ActivityVocabularyContainer.this, message, Toast.LENGTH_SHORT).show();
        //fontos frissiti a szavakat
        //Data.refreshDictionary(vocabularyID);
        numOfSelectedItems = 0;
        //delete

        selectable = false;
        lyt_options.setVisibility(View.GONE);
        listRefresh();
        titleRefresh();
        numOfSelectedItems = 0;

    }

    public void editWord(View view) {
        int indexOfWord = -1;

        for (Word word : vocabularies.get(indexOfVocabulary).getWordList())
            if (word.isSelected())
            {
                indexOfWord = word.getIndexList();
                word.setSelected(false);
                break;
            }

        Intent intent = new Intent(ActivityVocabularyContainer.this, EditWord.class);
        Bundle extras = new Bundle();

        extras.putInt("number", indexOfWord);
        extras.putString("vocabularyCode", vocabularyCode);
        extras.putInt("p", indexOfVocabulary);

        intent.putExtras(extras);
        startActivity(intent);
    }
}
