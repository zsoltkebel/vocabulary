package com.vocabulary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.screens.vocabulary.RecyclerViewAdapterPhrases;

import static com.vocabulary.screens.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.KEY_WORD_ID;

public class ActivityWord extends AppCompatActivity {

    private TextView txtv;
    private TextView txtvMeaning;

    private Word word;

    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        database = new DBHelper(getBaseContext());

        txtv = (TextView) findViewById(R.id.txtvWord);
        txtvMeaning = (TextView) findViewById(R.id.txtvMeaning);


        Bundle extras = getIntent().getExtras();
        final int indexOfWord = extras.getInt(KEY_WORD_ID);
        final String vocabularyCode = extras.getString(RecyclerViewAdapterPhrases.VOCABULARY_CODE);
        final int indexOfVocabulary = extras.getInt(KEY_INDEX_OF_VOCABULARY);

        word = database.getPhrase(vocabularyCode, indexOfWord);


        txtv.setText(word.getWord());
        txtvMeaning.setText(word.getWord2());
        txtv.setVisibility(View.VISIBLE);

        registerForContextMenu(findViewById(R.id.imageButton));
        //openContextMenu(findViewById(R.id.imageButton));
    }

    public void showPopup(View view){

        PopupMenu popup = new PopupMenu(ActivityWord.this, findViewById(R.id.imageButton));
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(ActivityWord.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

