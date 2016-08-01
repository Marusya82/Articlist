package com.mtanasyuk.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mtanasyuk.nytimessearch.R;
import com.mtanasyuk.nytimessearch.adapters.ArticlesRecycleViewAdapter;
import com.mtanasyuk.nytimessearch.fragments.SettingsDialogFragment;
import com.mtanasyuk.nytimessearch.models.Article;
import com.mtanasyuk.nytimessearch.models.EndlessScrollListener;
import com.mtanasyuk.nytimessearch.models.Filter;
import com.mtanasyuk.nytimessearch.models.ItemClickSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SettingsDialogFragment.SettingsDialogListener {

    final String apiKey = "6474c108b83f4af39476a770330f53b2";
    final String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    String query;
    Filter filter;

    @BindView(R.id.rvArticles) RecyclerView rvArticles;
    @BindView(R.id.toolbar) Toolbar toolbar;

    ArrayList<Article> articles;
    ArticlesRecycleViewAdapter adapter;
    SearchView searchView;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupViews();
    }

    public void setupViews() {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        articles = new ArrayList<>();
        adapter = new ArticlesRecycleViewAdapter(articles);
        rvArticles.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        // hook up listener with a decorator
        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                    Article article = articles.get(position);
                    i.putExtra("article", article);
                    startActivity(i);
                }
        );

        rvArticles.addOnScrollListener(new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchArticlesAsync(page);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            public boolean onQueryTextSubmit(String searchQuery) {
                // in some cases text submit fires several times, clear the focus
                searchView.clearFocus();
                articles.clear();
                int curSize = adapter.getItemCount();
                adapter.notifyItemRangeRemoved(0, curSize);
                adapter.notifyDataSetChanged();
                query = searchQuery;
                fetchArticlesAsync(0);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            FragmentManager fm = getSupportFragmentManager();
            SettingsDialogFragment settingDialog = SettingsDialogFragment.newInstance("Select advanced filters:");
            settingDialog.show(fm, "fragment_settings");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        miActionProgressItem.setVisible(false);
    }

    @Override
    public void onFinishEditDialog(Filter filterReturned) {
        Snackbar.make(findViewById(android.R.id.content), R.string.applied, Snackbar.LENGTH_LONG).show();
        filter = filterReturned;
        searchView.setQuery("", false);
        query = null;
        articles.clear();
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeRemoved(0, curSize);
        adapter.notifyDataSetChanged();
        fetchArticlesAsync(0);
    }

    public void fetchArticlesAsync(int page) {
        showProgressBar();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (query != null) {
            if (!query.isEmpty()) params.put("q", query);
        }
        if (filter != null) {
            if (!filter.getDate().isEmpty()) params.put("begin_date", filter.getDate());
            if (!filter.getSort().isEmpty()) params.put("sort", filter.getSort());
            if (filter.isArts() && filter.isFashion() && filter.isSports() ) {
                params.put("fq", "news_desk: (\"fashion & style\", \"sports\", \"arts\")");
            } else if (filter.isArts() && filter.isFashion()) {
                params.put("fq", "news_desk: (\"fashion & style\", \"arts\")");
            } else if (filter.isArts() && filter.isSports()) {
                params.put("fq", "news_desk: (\"sports\", \"arts\")");
            } else if (filter.isFashion() && filter.isSports()) {
                params.put("fq", "news_desk: (\"fashion & style\", \"sports\")");
            } else if (filter.isArts()) {
                params.put("fq", "news_desk:(\"arts\")");
            } else if (filter.isFashion()) {
                params.put("fq", "news_desk:(\"fashion & style\")");
            } else if (filter.isSports()) {
                params.put("fq", "news_desk:(\"sports\")");
            }
        }
        params.put("api-key", apiKey);
        params.put("page", page);
        if (isNetworkAvailable()) {
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJSONResults;
                    try {
                        articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                        int curSize = adapter.getItemCount();
                        ArrayList<Article> newItems = Article.fromJSONArray(articleJSONResults);
                        articles.addAll(newItems);
                        // curSize should represent the first element that got added
                        // newItems.size() represents the itemCount
                        adapter.notifyItemRangeInserted(curSize, articles.size() - 1);
                        hideProgressBar();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
