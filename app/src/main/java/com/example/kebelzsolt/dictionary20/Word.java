package com.example.kebelzsolt.dictionary20;

import android.database.Cursor;

import java.text.Normalizer;

/**
 * Created by Kébel Zsolt on 1/8/2017.
 */

public class Word {

    private int id;
    private int indexList;
    private String word1;
    private String word2;
    private Vocabulary vocabularyIn;
    boolean selected = false;

    public Word(){}

    public Word(String word1, String word2)
    {
        this.word1= cutSpaces(word1);
        this.word2 = cutSpaces(word2);
    }

    public Word(Cursor res)
    {
        id = res.getInt(res.getColumnIndex(DBHelper.COLUMN_ID));
        word1 = res.getString(res.getColumnIndex(DBHelper.WORDS_COLUMN_WORD));
        word2 = res.getString(res.getColumnIndex(DBHelper.WORDS_COLUMN_MEANING));
    }

    public void update(Word newPhrase)
    {
        this.word1 = newPhrase.getWord();
        this.word2 = newPhrase.getWord2();
    }

    public int getIndexList()
    {
        return indexList;
    }

    public void setIndexList(int index)
    {
        this.indexList = index;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int index)
    {
        id = index;
    }

    public String getWord()
    {
        return word1;
    }

    public void setWord(String word1)
    {
        this.word1 = cutSpaces(word1);
    }

    public String getWord2()
    {
        return word2;
    }

    public void setWord2(String word2)
    {
        this.word2 = cutSpaces(word2);
    }

    public void setVocabularyIn(Vocabulary vocabularyIn)
    {
        this.vocabularyIn = vocabularyIn;
    }

    public Vocabulary getVocabularyIn()
    {
        return vocabularyIn;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public boolean contains(String s)
    {
        if (s == null || s.equals(""))
            return true;
        if (removeAccents(word1).contains(removeAccents(s)) ||
                removeAccents(word2).contains(removeAccents(s)))
            return true;
        else
            return false;
    }

    private String cutSpaces(String string)
    {   // cuts down the spaces befora and after the string
        if (string.length() == 0)
            return string;
        int i = 0;
        if (string.charAt(0) == ' ')
        {
            for (i = 0; i < string.length() - 1 && string.charAt(i) == ' '; i++) {}
            string = string.substring(i);
        }
        if (string.charAt(string.length() - 1) == ' ')
        {
            for (i = string.length() - 1; i > 0 && string.charAt(i) == ' '; i--) {}
            string = string.substring(0, i + 1);
        }

        if (string.equals(" "))
            string = "";

        return string;
    }

    public boolean isEmpty()
    {
        if (word1.equals("") || word2.equals(""))
            return true;
        else
            return false;
    }

    public static String removeAccents(String text)
    {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public void appendMeaning(String meaning)
    {
        word2 += (", " + meaning);
    }
}
