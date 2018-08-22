package com.vocabulary;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Kébel Zsolt on 2018. 03. 04..
 */

public class Subject {

    public final static int LANGUAGE = 1;
    public final static int OTHER = 2;

    private String subject;
    private int icon;
    private int type;

    public Subject() {}

    public Subject(String name, int icon, int type)
    {
        this.subject = name;
        this.icon = icon;
        this.type = type;
    }

    public void setSubject(String name)
    {
        this.subject = name;
    }

    public String getSubject()
    {
        return subject;
    }

    public Drawable getIconDrawable(Context context)
    {
        return context.getDrawable(icon);
    }

    public void setIcon(int id)
    {
        this.icon = id;
    }

    public int getIconInt()
    {
        return icon;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    public int getType()
    {
        return type;
    }

    public int getSubjectResourceString(Context context)
    {
        return context.getResources().getIdentifier(subject.replaceAll("[0-9]", ""), "string", context.getPackageName());
    }
}
