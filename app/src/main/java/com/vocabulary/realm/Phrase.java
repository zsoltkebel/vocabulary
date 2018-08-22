package com.vocabulary.realm;

import java.text.DateFormat;
import java.text.Normalizer;
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
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    public Phrase() {
        date = dateFormat.format(new Date());
        p1 = null;
        p2 = null;
        language = null;
        vocabularyId = null;
    }

    public void set(String p1, String p2, Vocabulary vocabulary) {
        this.p1 = p1.trim();
        this.p2 = p2.trim();
        this.language = vocabulary.getLanguage();
        this.vocabularyId = vocabulary.getId();
    }

    public void setP1(String p1) {
        this.p1 = p1.trim();
    }

    public void setP2(String p2) {
        this.p2 = p2.trim();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setVocabularyId(String vocabularyId) {
        this.vocabularyId = vocabularyId;
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

    public boolean containsFilter(String filter) {
        if (filter == null || filter.equals(""))
            return true;
        else if (removeAccents(p1.toLowerCase()).contains(removeAccents(filter.toLowerCase())) ||
                removeAccents(p2.toLowerCase()).contains(removeAccents(filter.toLowerCase())))
            return true;
        else
            return false;
    }

    public static String removeAccents(String text) {
        return text == null ? null : Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
