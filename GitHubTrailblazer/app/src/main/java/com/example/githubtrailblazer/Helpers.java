package com.example.githubtrailblazer;

import android.content.Context;
import com.google.gson.Gson;
import java.io.*;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Helpers class
 */
public class Helpers {
    public static int MILLION = (int) Math.pow(10, 6);
    public static int THOUSAND = (int) Math.pow(10, 3);

    public static String formatCount(Integer count) {
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        if (count > MILLION) return (df.format(count.doubleValue() / MILLION)) + "M";
        if (count > THOUSAND) return (df.format(count.doubleValue() / THOUSAND)) + "K";
        return count.toString();
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
