package com.vocabulary.utils;

public class LanguageHelper {
    public static String ENGLISH = "en";
    public static String SPANISH = "es";
    public static String FRENCH = "fr";
    public static String GERMAN = "de";
    public static String ITALIAN = "it";
    public static String CHINESE = "zh-CN";
    public static String HUNGARIAN = "hu";

    public static String getLanguageCode(String language) {
        switch (language) {
            case "English":
                return ENGLISH;
            case "Spanish":
                return SPANISH;
            case "French":
                return FRENCH;
            case "German":
                return GERMAN;
            case "Italian":
                return ITALIAN;
            case "Chinese":
                return CHINESE;
            case "Hungarian":
                return HUNGARIAN;
            default:
                return ENGLISH;
        }
    }
}
