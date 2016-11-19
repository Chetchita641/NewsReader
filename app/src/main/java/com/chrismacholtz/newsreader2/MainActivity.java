package com.chrismacholtz.newsreader2;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SearchView.OnQueryTextListener {
    private ListView mListView;
    private TextView mEmptyView;
    private TextView mLatestNewsTextView;
    private TextView mUsNewsTextView;
    private TextView mWorldTextView;
    private TextView mBusinessTextView;
    private TextView mSportsTextView;
    private TextView mCultureTextView;
    private TextView mResultsTextView;
    private boolean mShowResults = false;
    private static int DARK;
    private static int SELECTED;
    private NewsAdapter mNewsAdapter;
    private int mLoaderId = 0;
    private int mQueryCounter = 0;
    private String mQueryUrl;
    private String mSearchQuery = "";
    private String mSection = "";
    private static final String BASE_SEARCH_URL = "https://content.guardianapis.com/search";
    private static final String API_KEY = "api-key=f4c2d001-c94c-45b6-aea8-af2c7bd356aa";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mLoaderId", mLoaderId);
        outState.putInt("mQueryCounter", mQueryCounter);
        outState.putBoolean("mShowResults", mShowResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //All Loader IDs 6 or greater belong to searches. Create a new loader for a new search. \
        //Also, blank out mSection to do a keyword search in the Loader
        mQueryCounter++;
        mLoaderId = 5 + mQueryCounter;
        mSearchQuery = query;
        mSection = "";
        mShowResults = true;

        getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLoaderId = savedInstanceState.getInt("mLoaderId");
            mQueryCounter = savedInstanceState.getInt("mQueryCounter");
            mShowResults = savedInstanceState.getBoolean("mShowResults");
        }

        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);
        mEmptyView = (TextView) findViewById(R.id.empty_text_view);
        mListView.setEmptyView(mEmptyView);

        mNewsAdapter = new NewsAdapter(getApplicationContext(), new ArrayList<News>());
        mLatestNewsTextView = (TextView) findViewById(R.id.latest_news_text_view);
        mUsNewsTextView = (TextView) findViewById(R.id.us_text_view);
        mWorldTextView = (TextView) findViewById(R.id.world_text_view);
        mBusinessTextView = (TextView) findViewById(R.id.business_text_view);
        mSportsTextView = (TextView) findViewById(R.id.sports_text_view);
        mCultureTextView = (TextView) findViewById(R.id.culture_text_view);

        //Check if there has been a search. If not, hide the results tab.
        mResultsTextView = (TextView) findViewById(R.id.results_text_view);
        showResults();

        //Constants for PrimaryDark and Primary colors
        DARK = getResources().getColor(R.color.colorPrimaryDark);
        SELECTED = getResources().getColor(R.color.colorPrimary);
        highlightSelection(mLoaderId);

        //onClickListeners for each of the tabs
        mLatestNewsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSection = "";
                mSearchQuery = "";
                mLoaderId = 0;
                mShowResults = false;
                highlightSelection(mLoaderId);
                getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
            }
        });

        mUsNewsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSection = "us-news";
                mSearchQuery = "";
                mLoaderId = 1;
                mShowResults = false;
                highlightSelection(mLoaderId);
                getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
            }
        });

        mWorldTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSection = "world";
                mSearchQuery = "";
                mLoaderId = 2;
                mShowResults = false;
                highlightSelection(mLoaderId);
                getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
            }
        });

        mBusinessTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSection = "business";
                mSearchQuery = "";
                mLoaderId = 3;
                mShowResults = false;
                highlightSelection(mLoaderId);
                getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
            }
        });

        mSportsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSection = "sport";
                mSearchQuery = "";
                mLoaderId = 4;
                mShowResults = false;
                highlightSelection(mLoaderId);
                getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
            }
        });

        mCultureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSection = "culture";
                mSearchQuery = "";
                mLoaderId = 5;
                mShowResults = false;
                highlightSelection(mLoaderId);
                getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
            }
        });

        getLoaderManager().initLoader(mLoaderId, null, MainActivity.this).forceLoad();
    }

    @Override
    public android.content.Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        mEmptyView.setText("");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_view);
        progressBar.setVisibility(View.VISIBLE);

        if (mNewsAdapter != null) {
            mNewsAdapter.clear();
        }

        //Build URI for Http query. If a keyword search is done, organize by relevance rather than newest.
        Uri baseUri = Uri.parse(BASE_SEARCH_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", mSearchQuery);
        if (mSection != "") {
            uriBuilder.appendQueryParameter("section", mSection);
        }

        if (mSearchQuery != "") {
            uriBuilder.appendQueryParameter("order-by", "relevance");
        } else {
            uriBuilder.appendQueryParameter("order-by", "newest");
        }

        mQueryUrl = uriBuilder.toString() + "&" + API_KEY;

        return new NewsLoader(getApplicationContext(), mQueryUrl);
    }

    @Override
    public void onLoadFinished(android.content.Loader<List<News>> loader, List<News> listOfNews) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_view);
        progressBar.setVisibility(View.GONE);

        //Test if there is an internet connection
        if (!haveConnectivity()) {
            mEmptyView.setText(R.string.no_connection);
        } else {
            //If no results came up, display error message
            if (listOfNews == null || listOfNews.isEmpty()) {
                showResults();
                mEmptyView.setText(R.string.no_results);
            } else {
                if (mNewsAdapter != null) {
                    mNewsAdapter.clear();
                }

                mNewsAdapter.addAll(listOfNews);
                updateUI();
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<News>> loader) {
        mNewsAdapter.clear();
    }

    //Check if there's a valid internet connection
    private boolean haveConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    //Update the UI
    private void updateUI() {
        mListView.setAdapter(mNewsAdapter);

        //If a user clicks on an item, the user goes to the Guardian website
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News clickedNews = mNewsAdapter.getItem(i);
                String webLink = clickedNews.getUrl();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(webLink));

                startActivity(intent);
            }
        });

        //If a search was done, then show the results tab
        showResults();
    }

    //Make all tabs dark. Used before the user's selection is highlighted.
    private void makeOthersDark() {
        mLatestNewsTextView.setBackgroundColor(DARK);
        mUsNewsTextView.setBackgroundColor(DARK);
        mWorldTextView.setBackgroundColor(DARK);
        mBusinessTextView.setBackgroundColor(DARK);
        mSportsTextView.setBackgroundColor(DARK);
        mCultureTextView.setBackgroundColor(DARK);
    }

    //Highlight the user's selection in the toolbar. If the LoaderId is 5>, then it has to be the Results tab.
    private void highlightSelection(int position) {
        makeOthersDark();
        switch (position) {
            case (0):
                mLatestNewsTextView.setBackgroundColor(SELECTED);
                break;
            case (1):
                mUsNewsTextView.setBackgroundColor(SELECTED);
                break;
            case (2):
                mWorldTextView.setBackgroundColor(SELECTED);
                break;
            case (3):
                mBusinessTextView.setBackgroundColor(SELECTED);
                break;
            case (4):
                mSportsTextView.setBackgroundColor(SELECTED);
                break;
            case (5):
                mCultureTextView.setBackgroundColor(SELECTED);
                break;
            default:
                mResultsTextView.setBackgroundColor(SELECTED);
                break;
        }
    }

    //Determines whether to show the Results tab
    private void showResults() {
        if (mShowResults) {
            mResultsTextView.setVisibility(View.VISIBLE);
            highlightSelection(mLoaderId);
        } else {
            mResultsTextView.setVisibility(View.GONE);
        }
    }
}
