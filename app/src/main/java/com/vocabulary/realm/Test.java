package com.vocabulary.realm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Test extends RealmObject {
    public static final String ID = "mDate";
    public static final String RIGHT_ANSWERS = "mRightAnswers";
    public static final String WRONG_ANSWERS = "mWrongAnswers";
    public static final String TEST_TYPE = "mTestType";

    @PrimaryKey
    private String mDate;

    private int mRightAnswers;
    private int mWrongAnswers;

    private int mTestType;

    @Ignore
    private static DateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    @Ignore
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public Test() {
        this.mDate = dateAndTimeFormat.format(new Date());
        this.mRightAnswers = 0;
        this.mWrongAnswers = 0;
        this.mTestType = 0;
    }

    public void incrementRightAnswers() {
        this.mRightAnswers++;
    }

    public void incrementWrongAnswers() {
        this.mWrongAnswers++;
    }

    public int getRightAnswers() {
        return mRightAnswers;
    }

    public int getWrongAnswers() {
        return mWrongAnswers;
    }

    public String getId() {
        return mDate;
    }
}
