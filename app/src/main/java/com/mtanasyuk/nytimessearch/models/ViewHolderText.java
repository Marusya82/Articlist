package com.mtanasyuk.nytimessearch.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mtanasyuk.nytimessearch.R;


public class ViewHolderText extends RecyclerView.ViewHolder {

    private TextView tvText;

    public ViewHolderText(View v) {
        super(v);
        tvText = (TextView) v.findViewById(R.id.tvText);
    }

    public TextView getText() {
        return tvText;
    }

    public void setTitleText(TextView text) {
        this.tvText = text;
    }
}
