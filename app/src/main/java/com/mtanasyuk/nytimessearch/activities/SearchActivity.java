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
import com.mtanasyuk.nytimessearch.models.SettingsDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

//    EditText etQuery;
    GridView gvResults;
//    Button btnSearch;

    final String apiKey = "6474c108b83f4af39476a770330f53b2";
    final String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    String query;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
    }

    public void setupViews() {
//        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
//        gvResults.setNestedScrollingEnabled(true);
//        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display an article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                // pass in that article into intent
                i.putExtra("article", article);
                // launch the activity
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                fetchArticlesAsync(page, "", "", "");
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchItem.expandActionView();
//        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return false;
            }

            public boolean onQueryTextSubmit(String searchQuery) {
                // get the value "query" which is entered in the search box
                adapter.clear();
                query = searchQuery;
                fetchArticlesAsync(0, "", "", "");
//                searchView.clearFocus();
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
//        int id = item.getItemId();
//        switch(id) {
//            case R.id.action_search:
//                break;
//            case R.id.action_settings:
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

//    public void onArticleSearch(View view) {
//        // clear up the adapter for the fresh search and send a request
//        adapter.clear();
//        fetchArticlesAsync(0, "", "", "");
//    }

    public void fetchArticlesAsync(int page, String queryCategory, String date, String sort) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
//        String query = etQuery.getText().toString();
        params.put("q", query);
//        params.put("fq", queryCategory);
//        params.put("begin_date", date);
//        params.put("sort", sort);
        params.put("api-key", apiKey);
        params.put("page", page);
        if (isNetworkAvailable()) {
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
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


    public void showEditDialog(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialogFragment settingDialogFragment = SettingsDialogFragment.newInstance("Some Title");
        settingDialogFragment.show(fm, "fragment_edit_name");
    }
}
