package com.chrismacholtz.newsreader2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SWS Customer on 11/10/2016.
 */

public class NewsAdapter extends ArrayAdapter<News> {
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    public NewsAdapter (Context context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);

        News currentNewsItem = getItem(position);

        //Inserts the Title, section, and date into news_item.xml
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.news_title_text_view);
        titleTextView.setText(currentNewsItem.getTitle());

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);
        sectionTextView.setText(currentNewsItem.getSection());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(currentNewsItem.getDate());

        return listItemView;
    }
}
