package com.mtanasyuk.nytimessearch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {

    public String getDate() {
        return date;
    }

    public boolean isArts() {
        return isArts;
    }

    public boolean isFashion() {
        return isFashion;
    }

    public boolean isSports() {
        return isSports;
    }

    public String getSort() {
        return sort;
    }

    String date;
    String sort;
    boolean isArts;
    boolean isFashion;
    boolean isSports;

    public Filter(String date, boolean isArts, boolean isFashion, boolean isSports, String sort) {
        this.date = date;
        this.isArts = isArts;
        this.isFashion = isFashion;
        this.isSports = isSports;
        this.sort = sort;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeByte(this.isArts ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFashion ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSports ? (byte) 1 : (byte) 0);
        dest.writeString(this.sort);
    }

    protected Filter(Parcel in) {
        this.date = in.readString();
        this.isArts = in.readByte() != 0;
        this.isFashion = in.readByte() != 0;
        this.isSports = in.readByte() != 0;
        this.sort = in.readString();
    }

    public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel source) {
            return new Filter(source);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };
}
