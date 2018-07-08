package com.example.kebelzsolt.dictionary20;

import android.content.Context;

import java.util.ArrayList;

import static com.example.kebelzsolt.dictionary20.Subject.*;

/**
 * Created by Kébel Zsolt on 1/18/2017.
 */

public class Data {

    public static DBHelper mydb = null;

    public static ArrayList<Vocabulary> vocabularies = new ArrayList<>();
    public static ArrayList<Subject> subjects = new ArrayList<>();

    public static int numOfSelectedItems;

    static final int languageTillIndex = 7;

    public Data(Context context)
    {
        mydb = new DBHelper(context);
        vocabularies = mydb.getAllVocabularies();
        this.numOfSelectedItems = 0;
        //TODO use them iconoc 200px
        subjects.add(new Subject("English", R.drawable.united_kingdom, LANGUAGE));
        subjects.add(new Subject("Spanish", R.drawable.spain, LANGUAGE));
        subjects.add(new Subject("French", R.drawable.france, LANGUAGE));
        subjects.add(new Subject("German", R.drawable.germany, LANGUAGE));
        subjects.add(new Subject("Italian", R.drawable.italy, LANGUAGE));
        subjects.add(new Subject("Chinese", R.drawable.china, LANGUAGE));
        subjects.add(new Subject("Hungarian", R.drawable.hungary, LANGUAGE));

        subjects.add(new Subject("Maths", R.drawable.maths, OTHER));
        subjects.add(new Subject("IT", R.drawable.it, OTHER));
        subjects.add(new Subject("Physics", R.drawable.physics, OTHER));
        subjects.add(new Subject("Literature", R.drawable.literature, OTHER));
        subjects.add(new Subject("Grammar", R.drawable.grammar, OTHER));
        subjects.add(new Subject("Geography", R.drawable.geography, OTHER));
        subjects.add(new Subject("Chemistry", R.drawable.chemistry, OTHER));
        subjects.add(new Subject("Biology", R.drawable.biology, OTHER));
    }

    public static void orderDicList() {
        ArrayList<Vocabulary> ordered = new ArrayList<>();
        int eddigRendezett = 0;
        boolean first = true;
        for (int x = 0; x < vocabularies.size();) {
            if (!vocabularies.get(x).getCode().contains("_")) {
                vocabularies.remove(x);
            } else {
                x++;
            }
        }
        for (int i = 0; i < subjects.size(); i++) {
            first = true;
            for (int l = eddigRendezett; l < vocabularies.size(); l++) {
                if (vocabularies.get(l).getCode().contains(subjects.get(i).getSubject())) {
                    Vocabulary dic = vocabularies.get(l);
                    dic.setType(subjects.get(i).getType());
                    dic.setIcon(subjects.get(i).getIconInt());
                    vocabularies.remove(l);
                    vocabularies.add(eddigRendezett, dic);

                    eddigRendezett++;
                }
            }
        }
        eddigRendezett = 0;
        for (int i = 0; i < subjects.size(); i++) {
            first = true;
            int numOf = 0;
            int indexOfContainer = 0;
            Subject subject = subjects.get(i);
            for (int l = eddigRendezett; l < vocabularies.size(); l++) {
                if (vocabularies.get(l).getCode().contains(subject.getSubject())) {
                    Vocabulary dic = new Vocabulary();
                    if (first) {
                        dic.setCode(subject.getSubject());
                        dic.setType(subject.getType());
                        dic.setSubject(subject.getSubject());
                        dic.setIcon(subject.getIconInt());
                        vocabularies.add(l, dic);
                        indexOfContainer = l;

                        first = false;
                        eddigRendezett++;
                        numOf += vocabularies.get(l).getNumOfWords();
                    } else {
                        numOf += vocabularies.get(l).getNumOfWords();
                        eddigRendezett++;
                    }
                    vocabularies.get(indexOfContainer).addWordList(vocabularies.get(l).getWordList());

                }

            }
        }

    }

    public ArrayList<Word> getWordListOfDic(int i) {
        return vocabularies.get(i).getWordList();
    }

    public static int getIndexOfSubject(String subject)
    {
        for (int i = 0; i < subjects.size(); i++)
            if (subjects.get(i).getSubject().equals(subject))
                return i;
        return 0;
    }
    public static void updateVocabularies()
    {
        vocabularies = mydb.getAllVocabularies();
        orderDicList();
    }

    public static void refreshDictionary(int indexOfVocabulary)
    {
        vocabularies.get(indexOfVocabulary).setWordList(mydb.getAllRecords(vocabularies.get(indexOfVocabulary)));
    }

    public static boolean addVocabuary(String name, String language)
    {
        String code = language + "_" + name.replace(" ", "_").replace(".", "__");
        return mydb.addLanguage(code);
    }
    public static boolean addVocabuary(String code)
    {
        return mydb.addLanguage(code);
    }

    public static void deleteVocabulary(String name, String language)
    {
        String code = language + "_" + name.replace(" ", "_").replace(".", "__");
        mydb.deleteTable(code);
    }

    public static boolean renameVocabulry(String oldName, String language, String newName)
    {
        String oldCode = language + "_" + oldName.replace(" ", "_"). replace(".", "__");
        String newCode = language + "_" + newName.replace(" ", "_"). replace(".", "__");

        if (contains(newCode))
            return false;
        else
        {
            mydb.renameTable(oldCode, newCode);
            return true;
        }

    }

    private static boolean contains(String code)
    {
        for (Vocabulary dic : vocabularies)
            if (dic.getCode().equals(code))
                return true;
        return false;
    }

    public static int getIconInt(String nameOfSubject)
    {
        for (Subject subject : subjects)
            if (subject.getSubject().equals(nameOfSubject))
                return subject.getIconInt();
        return 0;
    }

    public static int getTypeOfSubject(String nameOfSubject)
    {
        for (Subject subject : subjects)
            if (subject.getSubject().equals(nameOfSubject))
                return subject.getType();
        return 0;
    }

    public static int getIndexOfVocabulary(String codeOfVocabulary)
    {
        for (int i = 0; i < vocabularies.size(); i++)
            if (vocabularies.get(i).getCode().equals(codeOfVocabulary))
                return i;
        return 0;
    }

    public static boolean isSubject(String code)
    {
        for (Subject subject : subjects)
            if (code.contains(subject.getSubject() + "_"))
                return true;
        return false;
    }

    public static boolean isExisting()
    {
        if (mydb == null)
            return false;
        else
            return true;
    }
}
