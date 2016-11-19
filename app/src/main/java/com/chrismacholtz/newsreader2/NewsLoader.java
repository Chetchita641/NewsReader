package com.chrismacholtz.newsreader2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.Loader;

import java.util.List;

/**
 * Created by SWS Customer on 11/12/2016.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mQueryUrl;

    public NewsLoader (Context context, String queryUrl) {
        super(context);
        mQueryUrl = queryUrl;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        return QueryUtils.fetchNews(mQueryUrl);
    }
}
