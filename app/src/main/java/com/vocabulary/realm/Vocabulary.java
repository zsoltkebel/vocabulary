package com.vocabulary.realm;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vocabulary.JSONParser;
import com.vocabulary.Subject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Vocabulary extends RealmObject {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String LANGUAGE = "language";
    public static final String PHRASES = "phrases";
    public static final String SORT_STRING = "sortString";
    public static final String MARKED = "marked";

    @PrimaryKey
    private String id;
    @Required
    private String language;
    @Required
    private String title;
    private boolean marked;
    private RealmList<Phrase> phrases;

    @Required
    @Index
    private String sortString;
    @Required
    private String dateAndTime;

    @Ignore
    private static DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    @Ignore
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public Vocabulary() {
        phrases = new RealmList<>();
    }

    public void set(String language, String title) {
        this.id = title.equals("") ? language : language + "_" + dateAndTimeFormat.format(new Date());
        this.sortString = title.equals("") ? language : language + "_" + title;
        this.language = language.replaceAll("[0-9]", "");
        this.title = title;
        this.marked = false;
        this.dateAndTime = dateAndTimeFormat.format(new Date());
    }

    public void setTitle(String title) {
        this.title = title;
        int i = 0;
        while (i < sortString.length() && sortString.charAt(i) != '_')
            i++;
        this.sortString = sortString.substring(0, ++i) + title;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getId() {
        return id;
    }

    public String getSortString() {
        return sortString;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isMarked() {
        return marked;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public RealmList<Phrase> getPhrases() {
        return phrases;
    }

    public Phrase getPhraseAt(int index) {
        return phrases.get(index);
    }

    public String getLanguageReference(Context context) {
        return  context.getString(
                context.getResources().getIdentifier(getLanguage(), "string", context.getPackageName()));
    }

    public static DateFormat getDateAndTimeFormat() {
        return dateAndTimeFormat;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public String getString()
    {
        String result = "name: ";
        result += title;
        result += " language: ";
        result += language;
        result += " phrases: ";
        result += String.valueOf(phrases.size());

        return result;
    }

    public int getIconInt(Context context)
    {
        ArrayList<Subject> subjects = JSONParser.getSubjects(context);
        for (Subject subject : subjects)
            if (subject.getSubject().contains(language))
                return subject.getIconInt();
        Log.e("Vocabulary.java", "no such subject");
        return 0;
    }

    public Drawable getIconDrawable(Context context)
    {
        return ContextCompat.getDrawable(context, getIconInt(context));
    }
}
