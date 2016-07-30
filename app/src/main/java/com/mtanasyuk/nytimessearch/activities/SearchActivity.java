package com.mtanasyuk.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mtanasyuk.nytimessearch.R;
import com.mtanasyuk.nytimessearch.adapters.ArticleArrayAdapter;
import com.mtanasyuk.nytimessearch.models.Article;
import com.mtanasyuk.nytimessearch.models.EndlessScrollListener;
import com.mtanasyuk.nytimessearch.models.Filter;
import com.mtanasyuk.nytimessearch.models.SettingsDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SettingsDialogFragment.SettingsDialogListener {

    GridView gvResults;
    final String apiKey = "6474c108b83f4af39476a770330f53b2";
    final String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    String query;
    Filter filter;
    SearchView searchView;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
    }

    public void setupViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                fetchArticlesAsync(page);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setBackgroundColor(3);
        searchItem.expandActionView();
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return false;
            }

            public boolean onQueryTextSubmit(String searchQuery) {
                // in some cases text submit fires several times
                searchView.clearFocus();
                adapter.clear();
                query = searchQuery;
                fetchArticlesAsync(0);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            FragmentManager fm = getSupportFragmentManager();
            SettingsDialogFragment settingDialog = SettingsDialogFragment.newInstance("Set the filters:");
            settingDialog.show(fm, "fragment_settings");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditDialog(Filter filterReturned) {
        filter = filterReturned;
        searchView.setQuery("", false);
        query = null;
//        Toast.makeText(this, "Hi, " + filter.isArts(), Toast.LENGTH_SHORT).show();
        adapter.clear();

        fetchArticlesAsync(0);
    }

    public void fetchArticlesAsync(int page) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (query != null) {
            if (!query.isEmpty()) params.put("q", query);
        }
        if (filter != null) {
            if (!filter.getDate().isEmpty()) params.put("begin_date", filter.getDate());
            if (!filter.getSort().isEmpty()) params.put("sort", filter.getSort());
            if (filter.isArts() && filter.isFashion() && filter.isSports() ) {
                params.put("fq", "news_desk:("+ '"' + "Arts" + '"' + " " + '"' + "Fashion & Style" + '"' + " " + '"' + "Sports" + '"' + ")");
            } else if (filter.isArts() && filter.isFashion()) {
                params.put("fq", "news_desk:("+ '"' + "Arts" + '"' + " " + '"' + "Fashion & Style" + '"' + ")");
            } else if (filter.isArts() && filter.isSports()) {
                params.put("fq", "news_desk:("+ '"' + "Arts" + '"' + " " + '"' + "Sports" + '"' + ")");
            } else if (filter.isFashion() && filter.isSports()) {
                params.put("fq", "news_desk:("+ '"' + "Fashion & Style" + '"' + " " + '"' + "Sports" + '"' + ")");
            } else if (filter.isArts()) {
                params.put("fq", "news_desk:(" + '"' + "Arts" + '"' + ")");
            } else if (filter.isFashion()) {
                params.put("fq", "news_desk:("+ '"' + "Fashion & Style" + '"' + ")");
            } else if (filter.isSports()) {
                params.put("fq", "news_desk:("+ '"' + "Sports" + '"' + ")");
            }
        }
        params.put("api-key", apiKey);
        params.put("page", page);
        Log.d("DEBUG", params.toString());
        if (isNetworkAvailable()) {
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJSONResults = null;
                    try {
                        articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                        adapter.addAll(Article.fromJSONArray(articleJSONResults));
                        Log.d("DEBUG", articles.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
