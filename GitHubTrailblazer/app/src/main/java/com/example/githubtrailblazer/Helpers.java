package com.example.githubtrailblazer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Helpers class
 */
public class Helpers {
    private static SimpleDateFormat utcformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static HashMap<String, String> langColorMap;

    // count constants
    public static int MILLION = (int) Math.pow(10, 6);
    public static int THOUSAND = (int) Math.pow(10, 3);

    // time constants
    private static final long SECOND_MS = 1000L;
    private static final long MINUTE_MS = 60L * SECOND_MS;
    private static final long HOUR_MS = 60L * MINUTE_MS;
    private static final long DAY_MS = 24L * HOUR_MS;
    private static final long YEAR_MS = 365L * DAY_MS;

    /**
     * Concert UTC date time string to milliseconds
     * @param utc - the UTC date time string
     * @return the milliseconds representation
     */
    public static Long utcToMs(String utc) {
        try {
            return utcformat.parse(utc).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Format elapsed time
     * @param elapsed - the time to be formatted, in milliseconds
     * @return the formatted elapsed time
     */
    public static String formatElapsedTime(Long elapsed) {
        // get current time
        String utcNow = Instant.now().toString().substring(0, 19) + "Z";
        Long now = utcToMs(utcNow);

        // handle future invalid times
        if (utcNow == null || now == null || elapsed > now || elapsed <= 0) return null;

        // format elapsed
        long timeDistance = now - elapsed;
        if (timeDistance < MINUTE_MS) return "now";
        else if (timeDistance < HOUR_MS) return Math.round(timeDistance / MINUTE_MS) + "m";
        else if (timeDistance < DAY_MS) return Math.round(timeDistance / HOUR_MS) + "h";
        else if (timeDistance < YEAR_MS) return Math.round(timeDistance / DAY_MS) + "d";
        return Math.round(timeDistance / YEAR_MS) + "y";
    }

    /**
     * Format counts
     * @param count - the count to be formatted
     * @return the formatted count
     */
    public static String formatCount(Integer count) {
        if (count == null) return "0";
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        if (count > MILLION) return (df.format(count.doubleValue() / MILLION)) + "M";
        if (count > THOUSAND) return (df.format(count.doubleValue() / THOUSAND)) + "K";
        return count.toString();
    }

    /**
     * Retrieves the GitHub color corresponding to a programming language
     * @param context - the context
     * @param language - the language
     * @return the color
     */
    public static int getLanguageColor(Context context, String language) {
        // setup singleton language color map if not defined
        if (langColorMap == null) {
            Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();
            langColorMap = (HashMap<String, String>) Helpers.fromRawJSON(context, R.raw.github_lang_colors, mapType);
        }
        String hex = language == null ? null : langColorMap.get(language);
        return hex == null ? R.color.primary1 : Color.parseColor(hex);
    }

    /**
     * Get raw JSON, map to corresponding class instance(s)
     * @param context - the context
     * @param rawJsonId - the raw JSON resource id
     * @param t - the type
     * @return the resulting object
     */
    public static Object fromRawJSON(Context context, int rawJsonId, Type t) {
        InputStream is = context.getResources().openRawResource(rawJsonId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Gson().fromJson(writer.toString(), t);
    }
}
