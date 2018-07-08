package com.example.kebelzsolt.dictionary20.main.vocabulary;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.DBHelper;
import com.example.kebelzsolt.dictionary20.R;
import com.example.kebelzsolt.dictionary20.Vocabulary;
import com.example.kebelzsolt.dictionary20.Word;

import droidninja.filepicker.fragments.BaseFragment;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kebelzsolt.dictionary20.Vocabulary.EMPTY;
import static com.example.kebelzsolt.dictionary20.Vocabulary.EXISTING;
import static com.example.kebelzsolt.dictionary20.Vocabulary.NEW;
import static com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary.KEY_VOCABULARY_ID;
import static com.example.kebelzsolt.dictionary20.main.vocabulary.RecyclerViewAdapterPhrases.KEY_SORT;
import static com.example.kebelzsolt.dictionary20.main.vocabulary.RecyclerViewAdapterPhrases.PREFS_SORT;
import static com.example.kebelzsolt.dictionary20.main.vocabulary.RecyclerViewAdapterPhrases.SORT_DATE;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class FragmentAddPhrase extends BaseFragment
{
    View root;

    EditText wordInput;
    EditText meaningInput;
    Button btnAddPhrase;

    Bundle arguments;

    int indexOfVocabulary;
    Vocabulary vocabulary;
    ActivityVocabulary activity;

    DBHelper database;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_page_add_phrase;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        root = inflater.inflate(getFragmentLayout(), container, false);

        activity = (ActivityVocabulary) getActivity();
        arguments = getArguments();
        database = activity.database;

        indexOfVocabulary = arguments.getInt(KEY_VOCABULARY_ID);
        vocabulary = database.getVocabulary(indexOfVocabulary);

        wordInput = (EditText) root.findViewById(R.id.editText_word);
        meaningInput = (EditText) root.findViewById(R.id.editText_meaning);
        btnAddPhrase = (Button) root.findViewById(R.id.btn_add_phrase);

        meaningInput.requestFocus();
        hideKeyboard(false);

        btnAddPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phrase = wordInput.getText().toString();
                final String meaning = meaningInput.getText().toString();

                final int position;
                switch (vocabulary.addPhrase(new Word(phrase, meaning)))
                {
                    case EMPTY:
                        Toast.makeText(getContext(), "Fill both fields!", Toast.LENGTH_SHORT).show();
                        break;
                    case EXISTING:
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());

                        builder2.setMessage(R.string.dialog_append_phrase_title);
                        builder2.setNegativeButton(R.string.No, null);
                        builder2.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Word changedWord = vocabulary.getWord(phrase);
                                changedWord.appendMeaning(meaning);
                                vocabulary.changeWord(null, changedWord);

                                FragmentPhraseList.update();

                                vocabulary.updatePhraseList(getSortType());
                                activity.getFragmentPhrases().getAdapter().updateList(getSortType());
                                activity.titleRefresh();

                                clearTexts();
                                focusPhraseEditText();
                                ((ActivityVocabulary) getActivity()).clearEditText();

                                String info = "<font color='#e62e00'><b>" + phrase + "</b></font>";
                                Spanned infoText = Html.fromHtml(info + " has been appended");
                                Toast.makeText(getActivity(), infoText, Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder2.create().show();
                        break;
                    case NEW:
                        String info = "<font color='#e62e00'><b>" + phrase + "</b></font>";
                        Spanned infoText = Html.fromHtml(info + " has been added");
                        Toast.makeText(getActivity(), infoText, Toast.LENGTH_SHORT).show();

                        FragmentPhraseList.update();

                        vocabulary.updatePhraseList(getSortType());
                        activity.getFragmentPhrases().getAdapter().updateList(getSortType());
                        activity.titleRefresh();

                        clearTexts();
                        focusPhraseEditText();
                        ((ActivityVocabulary) getActivity()).clearEditText();

                        break;
                    default:
                        break;
                        //TODO list refresh nem jo
                }

            }
        });

        return root;
    }

    public int getIndexOfPhrase(String phrase)
    {
        for (int i = 0; i < vocabulary.getNumOfWords(); i++)
            if (vocabulary.getWordList().get(i).equals(phrase))
                return i;
        return 0;
    }

    public void clearTexts()
    {
        wordInput.setText("");
        meaningInput.setText("");
    }

    public void focusPhraseEditText()
    {
        wordInput.requestFocus();
        hideKeyboard(false);
    }

    private void hideKeyboard(boolean hide)
    {
        final InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (hide)
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        else
            inputMethodManager.showSoftInput(wordInput,InputMethodManager.SHOW_IMPLICIT);
    }

    public int getSortType()
    {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_SORT, MODE_PRIVATE);
        return prefs.getInt(KEY_SORT, SORT_DATE);
    }
}
