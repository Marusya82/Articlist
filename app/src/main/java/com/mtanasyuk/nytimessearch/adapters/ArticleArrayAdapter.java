package com.mtanasyuk.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtanasyuk.nytimessearch.R;
import com.mtanasyuk.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> acticles) {
        super(context, android.R.layout.simple_list_item_1, acticles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for current position
        Article article = getItem(position);
        // check if the view is being recycled
        // if not - inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        // find the image view
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);

        // clear out recycled image from the convertView from last time
        imageView.setImageResource(0);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        // populate the thumbnail image
        String thumbnail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbnail)) {
            // remote download the image in the background
            Picasso.with(getContext()).load(thumbnail).into(imageView);
        }

        return convertView;
    }
}
