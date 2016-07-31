package com.mtanasyuk.nytimessearch.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtanasyuk.nytimessearch.R;


// Used to cache the views within the item layout for fast access
public class ViewHolderImage extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView tvTitle;

    public ViewHolderImage(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.ivImage);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView image) {
        this.imageView = image;
    }

    public TextView getTitle() {
        return tvTitle;
    }

    public void setTitle(TextView title) {
        this.tvTitle = title;
    }

}
