package com.chrismacholtz.newsreader2;

import android.util.Log;

/**
 * Created by SWS Customer on 11/10/2016.
 */

public class News {
    private String mTitle;
    private String mYearString;
    private String mMonthString;
    private String mDayString;
    private String mSection;
    private String mUrl;

    public News(String title, String dateFull, String section, String url) {
        mTitle = title;
        mSection = section;
        mUrl = url;

        //Pull out the date from the timestamp
        String[] splitDateFull = dateFull.split("T");
        String[] splitDate = splitDateFull[0].split("-");

        mYearString = splitDate[0];
        mMonthString = splitDate[1];
        mDayString = splitDate[2];
    }

    //Returns the title of the news article
    public String getTitle() {
        return mTitle;
    }

    //Returns the date of the news article in format 'Nov 16th, 2006'
    public String getDate() {
        //Change the numbered month into a 3-character month, e.g. 'Nov'
        String monthStringName;
        switch (mMonthString) {
            case ("01"):
                monthStringName = "Jan";
                break;
            case ("02"):
                monthStringName = "Feb";
                break;
            case ("03"):
                monthStringName = "Mar";
                break;
            case ("04"):
                monthStringName = "Apr";
                break;
            case ("05"):
                monthStringName = "May";
                break;
            case ("06"):
                monthStringName = "Jun";
                break;
            case ("07"):
                monthStringName = "Jul";
                break;
            case ("08"):
                monthStringName = "Aug";
                break;
            case ("09"):
                monthStringName = "Sep";
                break;
            case ("10"):
                monthStringName = "Oct";
                break;
            case ("11"):
                monthStringName = "Nov";
                break;
            case ("12"):
                monthStringName = "Dec";
                break;
            default:
                monthStringName = "";
                break;
        }

        //If day has a leading '0', take it out
        if (mDayString.startsWith("0")) {
            mDayString = mDayString.substring(1);
        }

        //Append 'st', 'nd', 'rd', or 'th' to the day
        if (mDayString.endsWith("1"))
            mDayString += "st";
        else if (mDayString.endsWith("2"))
            mDayString += "nd";
        else if (mDayString.endsWith("3"))
            mDayString += "rd";
        if (mDayString.endsWith("0") || mDayString.endsWith("4") || mDayString.endsWith("5") || mDayString.endsWith("6") || mDayString.endsWith("7")
                || mDayString.endsWith("8") || mDayString.endsWith("9")) {
            mDayString += "th";
        }

        return (monthStringName + " " + mDayString + ", " + mYearString);
    }

    //Returns which section of the news the story comes from
    public String getSection() { return mSection; }

    //Returns the story's URL
    public String getUrl() {
        return mUrl;
    }
}
