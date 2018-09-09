package com.vocabulary.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LearnOverview extends RealmObject {
    public static final String ID = "mVocabularyId";
    public static final String DATE_OF_LAST_TEST = "mDateOfLastTest";
    public static final String TESTS = "mTests";

    @PrimaryKey
    String mVocabularyId;

    String mDateOfLastTest;

    RealmList<Test> mTests;

    public LearnOverview() {
        this.mTests = new RealmList<>();
    }

    public LearnOverview(String vocabularyId) {
        this.mVocabularyId = vocabularyId;
        this.mTests = new RealmList<>();
        this.mDateOfLastTest = "a";
    }

    public void setDateOfLastTest(String date) {
        this.mDateOfLastTest = date;
    }

    public String getVocabularyId() {
        return mVocabularyId;
    }
}
