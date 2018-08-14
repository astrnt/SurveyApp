package co.astrnt.surveyapp.utils;

import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.Locale;

import co.astrnt.qasdk.dao.JobApiDao;

public class WordUtils {

    public static SpannableStringBuilder makeSectionOfTextBold(String text, String textToBold) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (textToBold.length() > 0 && !textToBold.trim().equals("")) {

            //for counting start/end indexes
            String testText = text.toLowerCase(Locale.US);
            String testTextToBold = textToBold.toLowerCase(Locale.US);
            int startingIndex = testText.indexOf(testTextToBold);
            int endingIndex = startingIndex + testTextToBold.length();
            //for counting start/end indexes

            if (startingIndex < 0 || endingIndex < 0) {
                return builder.append(text);
            } else if (startingIndex >= 0 && endingIndex >= 0) {

                builder.append(text);
                builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
            }
        } else {
            return builder.append(text);
        }

        return builder;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static String capEachWord(String source) {
        StringBuilder result = new StringBuilder();
        String[] splitString = source.split(" ");
        for (String target : splitString) {
            result.append(Character.toUpperCase(target.charAt(0))).append(target.substring(1)).append(" ");
        }
        return result.toString().trim();
    }

    public static String getTypeAndLocation(JobApiDao job) {
        String jobTypeAndLocation;

        if (job.getType() != null && job.getLocation() != null) {
            jobTypeAndLocation = String.format("%s | %s",
                    capEachWord(job.getType().replace("_", " ")),
                    job.getLocation());
        } else if (job.getLocation() == null) {
            jobTypeAndLocation = capEachWord(job.getType().replace("_", " "));
        } else {
            jobTypeAndLocation = "";
        }

        return jobTypeAndLocation;
    }
}
