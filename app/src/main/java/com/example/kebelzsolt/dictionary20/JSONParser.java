package com.example.kebelzsolt.dictionary20;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.kebelzsolt.dictionary20.Subject.LANGUAGE;
import static com.example.kebelzsolt.dictionary20.Subject.OTHER;

public class JSONParser
{
    public static ArrayList<Subject> getSubjects(Context context) {
        ArrayList<Subject> subjects = new ArrayList<>();

        JSONObject json = null;
        //JSONArray languages;

        try {
            json = createJSONFromFile(context, R.raw.languages);
            JSONArray languages = json.optJSONArray("languages");

            JSONObject item;
            Subject subject;
            for (int i = 0; i < languages.length(); i++) {
                item = languages.getJSONObject(i);

                String name = item.getString("subject");
                String res = item.getString("res");
                int type = 0;
                if (item.getString("type").equals("language"))
                    type = LANGUAGE;
                else
                    type = OTHER;

                subject = new Subject(name, context.getResources()
                        .getIdentifier(res, "drawable", context.getPackageName()), type);

                subjects.add(subject);
            }

            return subjects;
        } catch (Exception e) {
            Log.e("JSON_ERROR", "error while parsing json file");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static JSONObject createJSONFromFile(Context context, int fileID) {

        JSONObject result = null;

        try {
            // Read file into string builder
            InputStream inputStream = context.getResources().openRawResource(fileID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            for (String line = null; (line = reader.readLine()) != null ; ) {
                builder.append(line).append("\n");
            }

            // Parse into JSONObject
            String resultStr = builder.toString();
            JSONTokener tokener = new JSONTokener(resultStr);
            result = new JSONObject(tokener);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private void loadTestFile(Context context) {
        JSONObject test = createJSONFromFile(context, R.raw.languages);
        // ... do awesome stuff with test data ...
    }
}
