package com.mtanasyuk.nytimessearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article implements Parcelable {

    String webURL;
    String headline;
    String thumbNail;

    public String getHeadline() {
        return headline;
    }

    public String getWebURL() {
        return webURL;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public Article(JSONObject jsonObject) {
        try {
            this.webURL = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            // CHECK THIS FOR LATER, SOMETIMES NOTHING IS RETURNED
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {

        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webURL);
        dest.writeString(this.headline);
        dest.writeString(this.thumbNail);
    }

    protected Article(Parcel in) {
        this.webURL = in.readString();
        this.headline = in.readString();
        this.thumbNail = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
