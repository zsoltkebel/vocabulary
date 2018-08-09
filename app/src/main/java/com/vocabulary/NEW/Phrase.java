package com.vocabulary.NEW;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Phrase extends RealmObject {

    public static final String DATE = "date";
    public static final String P1 = "p1";
    public static final String P2 = "p2";
    public static final String LANGUAGE = "language";
    public static final String VOCABULARY_ID = "vocabularyId";

    @PrimaryKey
    private String date;
    @Required
    @Index
    private String p1;
    @Required
    private String p2;
    @Required
    private String language;
    @Required
    private String vocabularyId;

    @Ignore
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public Phrase() {
        date = dateFormat.format(new Date());
        p1 = null;
        p2 = null;
        language = null;
        vocabularyId = null;
    }

    public void set(String p1, String p2, Vocabulary vocabulary) {
        this.p1 = p1;
        this.p2 = p2;
        this.language = vocabulary.getLanguage();
        this.vocabularyId = vocabulary.getId();
    }

    public String getDate() {
        return date;
    }

    public String getP1() {
        return p1;
    }

    public String getP2() {
        return p2;
    }

    public String getLanguage() {
        return language;
    }

    public String getVocabularyId() {
        return vocabularyId;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

}
