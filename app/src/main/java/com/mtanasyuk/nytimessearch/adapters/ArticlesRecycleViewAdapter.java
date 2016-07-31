package com.mtanasyuk.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mtanasyuk.nytimessearch.R;
import com.mtanasyuk.nytimessearch.models.Article;
import com.mtanasyuk.nytimessearch.models.ViewHolderImage;
import com.mtanasyuk.nytimessearch.models.ViewHolderText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticlesRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Article> mArticles;
    Context context;

    public ArticlesRecycleViewAdapter(ArrayList<Article> articles) {
        mArticles = articles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // there are only two types always - with thumbnail (image) that is 1 and not
        if (viewType == 1) {
            View v1 = inflater.inflate(R.layout.viewholder_image, viewGroup, false);
            viewHolder = new ViewHolderImage(v1);
        } else {
            View v2 = inflater.inflate(R.layout.viewholder_text, viewGroup, false);
            viewHolder = new ViewHolderText(v2);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder.getItemViewType() == 1) {
            ViewHolderImage vh1 = (ViewHolderImage) viewHolder;
            configureViewHolderImage(vh1, position);
        } else {
            ViewHolderText vh2 = (ViewHolderText) viewHolder;
            configureViewHolderText(vh2, position);
        }
    }

    private void configureViewHolderImage(ViewHolderImage vh1, int position) {
        Article article = mArticles.get(position);
        if (article != null) {
            ImageView imageView = vh1.getImageView();
            imageView.setImageResource(0);
            String thumbnail = article.getThumbNail();
            Picasso.with(context).load(thumbnail).fit().into(imageView);
            vh1.getTitle().setText(article.getHeadline());
        }
    }

    private void configureViewHolderText(ViewHolderText vh2, int position) {
        Article article = mArticles.get(position);
        vh2.getText().setText(article.getHeadline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.mArticles.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling
    @Override
    public int getItemViewType(int position) {
        if (!mArticles.get(position).getThumbNail().isEmpty()) return 1;
        else return 0;
    }
}
