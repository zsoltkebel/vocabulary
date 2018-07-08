package com.example.kebelzsolt.dictionary20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper implements Serializable {

    private static final String DATABASE_NAME = "database.db";
    private static final String LANGUAGES_TABLE_NAME = "languages";
    protected static final String LANGUAGES_COLUMN_LANGUAGE = "language";
    static final String COLUMN_ID = "id";
    static final String WORDS_COLUMN_WORD = "word";
    static final String WORDS_COLUMN_MEANING = "meaning";

    private static final String CHANGED = "changed";

    private Context context;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + LANGUAGES_TABLE_NAME + "(" +
                        COLUMN_ID + " integer primary key, " +
                        LANGUAGES_COLUMN_LANGUAGE + " varchar(100) UNIQUE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LANGUAGES_TABLE_NAME);
        onCreate(db);
    }

    ArrayList<Vocabulary> getAllVocabularies() {

        ArrayList<Vocabulary> vocabularies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + LANGUAGES_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()) {
            Vocabulary vocabulary = new Vocabulary(this, res);
            vocabularies.add(vocabulary);
            res.moveToNext();
        }
        res.close();


        return vocabularies;
    }

    public ArrayList<String> getAllLanguages() {

        ArrayList<String> languages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + LANGUAGES_TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()) {
            languages.add(res.getString(res.getColumnIndex(LANGUAGES_COLUMN_LANGUAGE)));
            res.moveToNext();
        }
        res.close();
        return languages;
    }

    private ArrayList<String> getAllLanguagesLowerCase() {
        ArrayList<String> languages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res;
        try {
            res = db.rawQuery("SELECT LOWER(" + LANGUAGES_COLUMN_LANGUAGE + ") AS "
                    + LANGUAGES_COLUMN_LANGUAGE + " FROM "
                    + LANGUAGES_TABLE_NAME, null);
            res.moveToFirst();
        } catch (SQLiteException exception) {
            //new ErrorMessageDialog(context, exception.getMessage());
            return null;
        }

        while(!res.isAfterLast()) {
            languages.add(res.getString(res.getColumnIndex(LANGUAGES_COLUMN_LANGUAGE)));
            res.moveToNext();
        }

        res.close();

        return languages;
    }

    boolean addLanguage(String language) {
        ArrayList<String> languages = getAllLanguagesLowerCase();
        String languageLC = language.toLowerCase();

        if (languages == null || languages.contains(languageLC)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANGUAGES_COLUMN_LANGUAGE, language);
        db.insert(LANGUAGES_TABLE_NAME, null, contentValues);
        db.execSQL(
                "create table if not exists " + language + "(" +
                        COLUMN_ID + " integer primary key, " +
                        WORDS_COLUMN_WORD + " varchar(100) UNIQUE, " +
                        WORDS_COLUMN_MEANING + " varchar(100))"
        );
        /*
        try {
            db.execSQL("CREATE INDEX " + language + "_" + WORDS_COLUMN_WORD + "_idx ON "
                    + language + " (" + WORDS_COLUMN_WORD +")");
        } catch (SQLiteException exception) {
            //new ErrorMessageDialog(context, exception.getMessage());
            return false;
        }*/
        return true;
    }

    void renameTable(final String oldName, final String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LANGUAGES_COLUMN_LANGUAGE, newName);
        db.update(LANGUAGES_TABLE_NAME, contentValues, LANGUAGES_COLUMN_LANGUAGE + "= ? ", new String[] { oldName } );
        db.execSQL("ALTER TABLE " + oldName + " RENAME TO " + CHANGED);
        db.execSQL("ALTER TABLE " + CHANGED + " RENAME TO " + newName);

    }

    boolean insertWord(String language, Word word, Vocabulary vocabulary) {

        for (Word wordExisting : getAllRecords(vocabulary))
            if (wordExisting.getWord().equals(word.getWord()))
                return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORDS_COLUMN_WORD, word.getWord());
        contentValues.put(WORDS_COLUMN_MEANING, word.getWord2());
        db.insert(language, null, contentValues);
        db.rawQuery("SELECT * FROM "+ language
               +" ORDER BY "+ WORDS_COLUMN_WORD +" COLLATE NOCASE;", null);

        return true;

    }

    Cursor getDataById(String language, int number) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + language, null);
        res.moveToFirst();
        while(res.getInt(res.getColumnIndex(COLUMN_ID)) != number) {
            res.moveToNext();
        }
        return res;
    }

    boolean updateWord(String TABLE, Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORDS_COLUMN_WORD, word.getWord());
        contentValues.put(WORDS_COLUMN_MEANING, word.getWord2());
        db.update(TABLE, contentValues, COLUMN_ID + "= ? ", new String[] { Integer.toString(word.getId()) } );

        return true;
    }
/* TODO delete más
    public Integer deleteWord(String TABLE, Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer result = db.delete(TABLE,
                COLUMN_ID + "= ? ",
                new String[] { Integer.toString(word.getId()) });
        long lastID = DatabaseUtils.longForQuery(db, "SELECT MAX(id) FROM " + TABLE, null);
        if (lastID > word.getId())
            db.execSQL("UPDATE " + TABLE + " SET id = " + word.getId() + " WHERE id = " + lastID);

        return result;
    }*/
    public Integer deleteWord(String TABLE, Word word) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer result = db.delete(TABLE,
                COLUMN_ID + "= ? ",
                new String[] { Integer.toString(word.getId()) });
        return result;
    }
    public Integer deletePhrase(String TABLE, int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer result = db.delete(TABLE,
                COLUMN_ID + "= ? ",
                new String[] { Integer.toString(ID) });
        return result;
    }

    void deleteTable(String TABLE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LANGUAGES_TABLE_NAME,
                LANGUAGES_COLUMN_LANGUAGE + "= ? ",
                new String[] { TABLE });
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);


    }
    //TODO rendezes
/*
    public ArrayList<Word> getAllRecords(Vocabulary vocabularyTo) {
        ArrayList<Word> words = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_ID, WORDS_COLUMN_WORD, WORDS_COLUMN_MEANING};
        Cursor res = db.query(vocabularyTo.getCode(), columns,
                null, null, null, null, WORDS_COLUMN_WORD + " ASC");  // note the missing last null for 'limit'
        int index = 0;
        while(res.moveToNext()) {

            Word word = new Word();
            word.setIndexList(index++);
            word.setId(res.getInt(res.getColumnIndex(COLUMN_ID)));
            word.setWord(res.getString(res.getColumnIndex(WORDS_COLUMN_WORD)));
            word.setWord2(res.getString(res.getColumnIndex(WORDS_COLUMN_MEANING)));
            word.setVocabularyIn(vocabularyTo);
            words.add(word);
        }
        res.close();

        return words;
    } */

    public ArrayList<Integer> getOrderingArrayByDate(Vocabulary vocabularyTo)
    {
        ArrayList<Integer> orderingArray = new ArrayList<>();

        for (int i = 0; i < vocabularyTo.getNumOfWords(); i++)
            orderingArray.add(i);

        return orderingArray;
    } // TODO ordering
    public ArrayList<Integer> getOrderingArrayByABC(Vocabulary vocabularyTo)
    {
        ArrayList<Integer> orderingArray = new ArrayList<>();

        ArrayList<Word> ordered = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_ID, WORDS_COLUMN_WORD, WORDS_COLUMN_MEANING};
        Cursor res = db.query(vocabularyTo.getCode(), columns,
                null, null, null, null, WORDS_COLUMN_WORD + " ASC");  // note the missing last null for 'limit'
        int index = 0;
        while(res.moveToNext()) {

            Word word = new Word();
            word.setIndexList(index++);
            word.setId(res.getInt(res.getColumnIndex(COLUMN_ID)));
            word.setWord(res.getString(res.getColumnIndex(WORDS_COLUMN_WORD)));
            word.setWord2(res.getString(res.getColumnIndex(WORDS_COLUMN_MEANING)));
            word.setVocabularyIn(vocabularyTo);
            ordered.add(word);
        }
        res.close();

        ArrayList<Word> unOrdered = getAllRecords(vocabularyTo);
        for (int i = 0; i < ordered.size(); i++)
        {
            for (int l = 0; l < unOrdered.size(); l++)
            {
                if (unOrdered.get(l).getId() == ordered.get(i).getId())
                {
                    orderingArray.add(l);
                    break;
                }
            }
        }

        return orderingArray;
    }

    public Word getPhrase(String table, int ID)
    {
        Word phrase = new Word(getRecord(table, ID));
        return phrase;
    }

    public Vocabulary getVocabulary(int ID)
    {
        Vocabulary vocabulary = new Vocabulary(this, getRecord(LANGUAGES_TABLE_NAME, ID));
        return vocabulary;
    }

    public Cursor getRecord(String tableName, int ID)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        /*
        Cursor res = db.rawQuery( "SELECT * FROM " + tableName + " WHERE " +
                COLUMN_ID + "=?", new String[] { Integer.toString(ID) } );
*/
        Cursor res = db.rawQuery("select * from " + tableName, null);
        res.moveToFirst();
        while(!res.isAfterLast() && res.getInt(res.getColumnIndex(COLUMN_ID)) != ID)
            res.moveToNext();

        return res;
    }

    public ArrayList<Word> getAllRecords(Vocabulary vocabularyTo) {
        ArrayList<Word> words = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_ID, WORDS_COLUMN_WORD, WORDS_COLUMN_MEANING};
        Cursor res = db.rawQuery("select * from " + vocabularyTo.getCode(), null);
        int index = 0;
        while(res.moveToNext()) {
            /*
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, index);
            db.update(vocabularyTo.getCode(), contentValues, "id= ?", new String[] { Integer.toString(
                    res.getInt(res.getColumnIndex("id"))) });
*/
            Word word = new Word();
            word.setIndexList(index++);
            word.setId(res.getInt(res.getColumnIndex(COLUMN_ID)));
            word.setWord(res.getString(res.getColumnIndex(WORDS_COLUMN_WORD)));
            word.setWord2(res.getString(res.getColumnIndex(WORDS_COLUMN_MEANING)));
            word.setVocabularyIn(vocabularyTo);
            words.add(word);
        }
        res.close();

        return words;
    }
}
