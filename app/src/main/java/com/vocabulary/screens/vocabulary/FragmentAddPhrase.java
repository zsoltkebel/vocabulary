package com.vocabulary.screens.vocabulary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;

import droidninja.filepicker.fragments.BaseFragment;
import io.realm.Realm;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class FragmentAddPhrase extends BaseFragment
{
    View root;

    EditText wordInput;
    EditText meaningInput;
    Button btnAddPhrase;

    Realm realm;
    Vocabulary vocabulary;
    ActivityVocabulary activity;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_page_add_phrase;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        realm = ((ActivityVocabulary) getActivity()).getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        root = inflater.inflate(getFragmentLayout(), container, false);

        activity = (ActivityVocabulary) getActivity();

        vocabulary = ((ActivityVocabulary) getActivity()).getVocabulary();

        wordInput = (EditText) root.findViewById(R.id.editText_word);
        meaningInput = (EditText) root.findViewById(R.id.editText_meaning);
        btnAddPhrase = (Button) root.findViewById(R.id.btn_add_phrase);

        meaningInput.requestFocus();
        hideKeyboard(false);

        btnAddPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String p1 = wordInput.getText().toString();
                final String p2 = meaningInput.getText().toString();

                if (p1.equals("") || p2.equals("")) {

                } else if (realm.where(Phrase.class)
                        .equalTo(Phrase.LANGUAGE, vocabulary.getLanguage())
                        .equalTo(Phrase.P1, p1)
                        .findFirst() != null) {
                    Toast.makeText(getContext(), "already here", Toast.LENGTH_SHORT).show();
                } else {
                    final Phrase phrase = new Phrase();
                    phrase.set(p1, p2, vocabulary);

                    realm.beginTransaction();
                    final Phrase managedPhrase = realm.copyToRealm(phrase);
                    vocabulary.getPhrases().add(managedPhrase);
                    realm.commitTransaction();

                    //activity.getFragmentPhrases().getAdapter().updateList(realm);

                    //TODO switch to cnackbar in the future
                    Toast.makeText(getContext(), R.string.msg_added, Toast.LENGTH_SHORT).show();

                    wordInput.setText("");
                    meaningInput.setText("");

                    wordInput.requestFocus();

                    /*
                    for (int i = 0; i < 1000; i++) {
                        Phrase temp2 = new Phrase();
                        temp2.set(String.valueOf(i), String.valueOf(i), vocabulary);
                        mRealm.beginTransaction();
                        final Phrase temp = mRealm.copyToRealm(temp2);
                        vocabulary.getPhrases().add(temp);
                        mRealm.commitTransaction();
                    }*/
                }
            }
        });
/*
        btnAddPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phrase = wordInput.getText().toString();
                final String meaning = meaningInput.getText().toString();

                final int position;
                switch (vocabulary.insertPhrase(new Word(phrase, meaning)))
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

                                //FragmentPhrases.update();

                                vocabulary.updatePhraseList(getSortType());
                                //activity.getFragmentPhrases().getAdapter().updateList(getSortType());
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

                        //FragmentPhrases.update();

                        vocabulary.updatePhraseList(getSortType());
                        //activity.getFragmentPhrases().getAdapter().updateList(getSortType());
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
*/
        return root;
    }

    public int getIndexOfPhrase(String phrase)
    {
        /*
        for (int i = 0; i < vocabulary.getNumOfWords(); i++)
            if (vocabulary.getWordList().get(i).equals(phrase))
                return i;
                */
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

}
